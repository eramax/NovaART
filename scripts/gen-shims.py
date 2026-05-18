#!/usr/bin/env python3
"""
gen-shims.py — NovaART Android shim generator.

Scans APK DEX files, extracts every android.* / androidx.* class+method
reference, compares against src/java/nova-shims/, and writes stub Java
files for everything missing.

The APK directory is scanned recursively — drop any .apk file into apks/
(or any subdirectory) and it is picked up automatically next run.

Usage:
    python3 scripts/gen-shims.py [options]

    --apks DIR[,DIR,…]   Subdirs of apks/ to scan, or 'all' for every .apk
                         under apks/.  Default: scan everything under apks/.
    --dry-run            Print what would be generated, don't write files
    --report             Write coverage report to out/shim-coverage.txt
    --out DIR            Output directory (default: src/java/nova-shims)
"""

import argparse
import os
import struct
import zipfile
import sys
import json
import re
from collections import defaultdict
from pathlib import Path

# ─── root paths ──────────────────────────────────────────────────────────────

SCRIPT_DIR = Path(__file__).resolve().parent
ROOT       = SCRIPT_DIR.parent
SHIMS_DIR  = ROOT / "src" / "java" / "nova-shims"
APKS_ROOT  = ROOT / "apks"
OUT_REPORT = ROOT / "out" / "shim-coverage.txt"


def resolve_apk_paths(apks_arg: str | None) -> list[Path]:
    """
    Return a sorted list of .apk paths to scan.

    If --apks is omitted or 'all', return every .apk found recursively under
    apks/.  Otherwise treat each comma-separated token as a subdirectory name
    under apks/ and return all .apk files within those subdirs (recursively).
    """
    if not apks_arg or apks_arg.lower() == "all":
        roots = [APKS_ROOT]
    else:
        subdirs = [s.strip() for s in apks_arg.split(",") if s.strip()]
        roots = [APKS_ROOT / s for s in subdirs]

    paths: list[Path] = []
    for root in roots:
        if not root.exists():
            print(f"  warning: {root} does not exist, skipping", file=sys.stderr)
            continue
        paths.extend(sorted(root.rglob("*.apk")))
    return paths

# ─── DEX type descriptor helpers ─────────────────────────────────────────────

_PRIM = {"V":"void","Z":"boolean","B":"byte","S":"short","C":"char",
         "I":"int","J":"long","F":"float","D":"double"}

def desc_to_java(desc: str, stub_safe: bool = False) -> str:
    """Landroid/view/View; → android.view.View   [I → int[]

    If stub_safe=True, multi-level inner class types (Outer$Inner$Deep) are
    replaced with Object so generated stubs compile without needing the deeply
    nested inner class to be declared.
    """
    if not desc:
        return "Object"
    arrays = 0
    while desc.startswith("["):
        arrays += 1
        desc = desc[1:]
    if desc in _PRIM:
        base = _PRIM[desc]
    elif desc.startswith("L") and desc.endswith(";"):
        inner = desc[1:-1]
        dollar_count = inner.count("$")
        if stub_safe and dollar_count > 1:
            # e.g. Landroid/app/Notification$BubbleMetadata$Builder; → Object
            return "Object" + "[]" * arrays
        base = inner.replace("/", ".").replace("$", ".")
    else:
        base = desc
    return base + "[]" * arrays

def desc_to_classname(desc: str) -> str:
    """Landroid/view/View; → android.view.View  (keeps $ for inner)"""
    if desc.startswith("L") and desc.endswith(";"):
        return desc[1:-1].replace("/", ".")
    return desc

def default_return(java_type: str) -> str:
    if java_type in ("void",):           return ""
    if java_type in ("boolean",):        return "return false;"
    if java_type in ("byte","short","int","char","long"): return "return 0;"
    if java_type in ("float","double"):  return "return 0f;" if java_type=="float" else "return 0.0;"
    return "return null;"

# ─── DEX parser ──────────────────────────────────────────────────────────────

def _rul(data, pos):
    v = 0; shift = 0
    while True:
        b = data[pos]; pos += 1
        v |= (b & 0x7f) << shift; shift += 7
        if not (b & 0x80):
            return v, pos

def parse_dex(data: bytes) -> dict:
    """
    Returns dict keyed by android/androidx class descriptor:
    {
      "Landroid/view/View;": {
          "methods": { "methodName": [("Ljava/lang/String;I", "Z"), ...] },
          "fields":  { "fieldName": "Ljava/lang/String;" },
      }
    }
    Method signature tuple: (param_descs_joined_with_comma, return_type_desc)
    """
    if len(data) < 112 or not data[:4] in (b"dex\n", b"\x64\x65\x78\x0a"):
        if not data[:4] == b"dex\n":
            return {}

    (si, so, ti, to_, pri, pro, fi, fo, mi, mo) = struct.unpack_from("<10I", data, 56)

    # strings
    strs = []
    for i in range(si):
        off = struct.unpack_from("<I", data, so + i * 4)[0]
        _, s_start = _rul(data, off)
        end = s_start
        while data[end]: end += 1
        strs.append(data[s_start:end].decode("utf-8", errors="replace"))

    # types
    typs = [strs[struct.unpack_from("<I", data, to_ + i * 4)[0]] for i in range(ti)]

    # proto_ids → (return_type_desc, [param_type_descs])
    protos = []
    for i in range(pri):
        base = pro + i * 12
        _, ret_idx, params_off = struct.unpack_from("<III", data, base)
        ret = typs[ret_idx] if ret_idx < ti else "V"
        params = []
        if params_off:
            count = struct.unpack_from("<I", data, params_off)[0]
            for j in range(count):
                pidx = struct.unpack_from("<H", data, params_off + 4 + j * 2)[0]
                params.append(typs[pidx] if pidx < ti else "V")
        protos.append((ret, params))

    result = {}

    def get_cls(desc):
        if not (desc.startswith("Landroid/") or desc.startswith("Landroidx/")):
            return None
        if desc not in result:
            result[desc] = {"methods": defaultdict(list), "fields": {}}
        return result[desc]

    # fields
    for i in range(fi):
        ci, type_i, ni = struct.unpack_from("<HHI", data, fo + i * 8)
        if ci >= ti: continue
        cls_desc = typs[ci]
        c = get_cls(cls_desc)
        if c is None: continue
        fname = strs[ni] if ni < si else "?"
        ftype = typs[type_i] if type_i < ti else "V"
        c["fields"][fname] = ftype

    # methods
    for i in range(mi):
        ci, pi, ni = struct.unpack_from("<HHI", data, mo + i * 8)
        if ci >= ti or pi >= pri: continue
        cls_desc = typs[ci]
        c = get_cls(cls_desc)
        if c is None: continue
        mname = strs[ni] if ni < si else "?"
        ret, params = protos[pi]
        c["methods"][mname].append((params, ret))

    # ensure every android/* type that appears in method signatures also has
    # an entry so inner classes referenced only as parameter types get stubs
    for ret, params in protos:
        for desc in params + [ret]:
            # strip array prefix
            d = desc.lstrip("[")
            get_cls(d)

    return result

def scan_apk(path: Path) -> dict:
    """Parse all DEX files in an APK and merge results."""
    merged = {}
    try:
        with zipfile.ZipFile(path) as z:
            dex_names = sorted(n for n in z.namelist() if re.match(r"classes\d*\.dex", n))
            for name in dex_names:
                for desc, info in parse_dex(z.read(name)).items():
                    if desc not in merged:
                        merged[desc] = {"methods": defaultdict(list), "fields": {}}
                    for mn, sigs in info["methods"].items():
                        merged[desc]["methods"][mn].extend(sigs)
                    merged[desc]["fields"].update(info["fields"])
    except Exception as e:
        print(f"  WARNING: could not parse {path.name}: {e}", file=sys.stderr)
    return merged

# ─── shim existence checker ───────────────────────────────────────────────────

def shim_file_for(classname: str) -> Path:
    """android.view.View → src/java/nova-shims/android/view/View.java"""
    outer = classname.split("$")[0]
    return SHIMS_DIR / Path(outer.replace(".", "/") + ".java")

def shim_exists(classname: str) -> bool:
    return shim_file_for(classname).exists()

def inner_name(classname: str) -> str | None:
    """android.view.View$OnClickListener → OnClickListener; or None for top-level."""
    if "$" in classname:
        return classname.split("$")[-1]
    return None

def shim_has_method(classname: str, method_name: str) -> bool:
    f = shim_file_for(classname)
    if not f.exists():
        return False
    try:
        return method_name in f.read_text()
    except:
        return False

# ─── stub generator ──────────────────────────────────────────────────────────

# Known interface heuristics — class name suffixes that imply interface
_IFACE_SUFFIXES = (
    "Listener", "Callback", "Observer", "Handler", "Executor",
    "Interceptor", "Provider", "Resolver", "Formatter", "Comparator",
    "Runnable", "Callable",
)
# Exact fully-qualified names that are definitely interfaces
_KNOWN_INTERFACES = {
    "android.os.Parcelable",
    "android.os.IBinder",
    "android.os.IInterface",
    "android.os.Parcelable.Creator",
    "android.os.Parcelable.ClassLoaderCreator",
    "android.view.Choreographer.FrameCallback",
    "android.view.KeyEvent.Callback",
    "android.view.MenuItem.OnMenuItemClickListener",
    "android.view.View.OnClickListener",
    "android.view.View.OnTouchListener",
    "android.view.View.OnKeyListener",
    "android.content.ServiceConnection",
    "android.content.DialogInterface.OnClickListener",
    "android.content.DialogInterface.OnDismissListener",
    "android.content.DialogInterface.OnCancelListener",
    "android.database.Cursor",
    "android.graphics.drawable.Drawable.Callback",
    "android.view.SurfaceHolder",
    "android.view.SurfaceHolder.Callback",
    "android.view.SurfaceHolder.Callback2",
}
# Known abstract class heuristics
_ABSTRACT_PREFIXES = ("Abstract",)

# Common known superclasses for well-known patterns
_KNOWN_SUPERS: dict[str, tuple[str, list[str]]] = {
    # (superclass, [interfaces])
    "android.app.Activity":           ("android.app.Activity", []),
    "android.app.Service":            ("android.app.Service", []),
    "android.view.View":              ("android.view.View", []),
    "android.view.ViewGroup":         ("android.view.ViewGroup", []),
    "android.widget.TextView":        ("android.widget.TextView", []),
    "android.widget.FrameLayout":     ("android.widget.FrameLayout", []),
    "android.widget.LinearLayout":    ("android.widget.LinearLayout", []),
    "android.graphics.drawable.Drawable": ("android.graphics.drawable.Drawable", []),
    "android.content.ContentProvider":("android.content.ContentProvider", []),
    "android.content.BroadcastReceiver":("android.content.BroadcastReceiver", []),
}

def classify(classname: str) -> str:
    """Return 'interface', 'abstract class', or 'class'."""
    if classname in _KNOWN_INTERFACES:
        return "interface"
    simple = classname.split(".")[-1].split("$")[-1]
    if any(simple.endswith(s) for s in _IFACE_SUFFIXES):
        return "interface"
    if simple in _IFACE_SUFFIXES:
        return "interface"
    if any(simple.startswith(s) for s in _ABSTRACT_PREFIXES):
        return "abstract class"
    return "class"

def java_type_param(desc: str, idx: int) -> str:
    """Convert descriptor to Java type for a parameter."""
    return desc_to_java(desc)

def make_method_stub(name: str, params: list[str], ret_desc: str,
                     kind: str, used_names: set) -> str:
    """Generate a single method stub. Returns Java source lines."""
    ret_java = desc_to_java(ret_desc, stub_safe=True)
    pnames = [f"p{i}" for i in range(len(params))]
    param_str = ", ".join(f"{desc_to_java(p, stub_safe=True)} {pnames[i]}" for i, p in enumerate(params))
    ret_stmt = default_return(ret_java)

    if kind == "interface":
        return f"    {ret_java} {name}({param_str});"
    else:
        body = f" {{ {ret_stmt} }}" if ret_stmt else " {}"
        return f"    public {ret_java} {name}({param_str}){body}"

GENERATED_MARKER = "// @generated by gen-shims.py — do not edit by hand"

def generate_outer_class(classname: str, info: dict,
                         inner_classes: dict[str, dict]) -> str:
    """
    Generate complete Java source for one top-level class including
    all its inner classes.

    classname:     e.g. android.view.ViewParent
    info:          methods/fields from DEX (may be None if only inner refs)
    inner_classes: { "InnerName": info_dict }
    """
    pkg = ".".join(classname.split(".")[:-1])
    simple = classname.split(".")[-1]
    kind = classify(classname)

    lines = [GENERATED_MARKER, f"package {pkg};", ""]

    # class declaration
    if kind == "interface":
        lines.append(f"public interface {simple} {{")
    elif kind == "abstract class":
        lines.append(f"public abstract class {simple} {{")
    else:
        lines.append(f"public class {simple} {{")

    if info:
        _emit_members(lines, info, kind, simple)

    # inner classes / interfaces
    for inner_simple, iinfo in sorted(inner_classes.items()):
        lines.append("")
        inner_kind = classify(f"{classname}.{inner_simple}")
        if inner_kind == "interface":
            lines.append(f"    public interface {inner_simple} {{")
        elif inner_kind == "abstract class":
            lines.append(f"    public abstract static class {inner_simple} {{")
        else:
            lines.append(f"    public static class {inner_simple} {{")
        if iinfo:
            _emit_members(lines, iinfo, inner_kind, inner_simple, indent="    ")
        lines.append("    }")

    lines.append("}")
    lines.append("")
    return "\n".join(lines)

def _emit_members(lines: list, info: dict, kind: str,
                  simple: str, indent: str = "") -> None:
    used = set()
    for mname, sigs in sorted(info["methods"].items()):
        if mname in ("<init>", "<clinit>"):
            continue
        # emit the first signature only (avoid duplicate method names unless
        # params differ — track by (name, param_count) to allow overloads)
        seen_sigs = set()
        for params, ret in sigs:
            key = (mname, len(params))
            if key in seen_sigs:
                continue
            seen_sigs.add(key)
            stub = make_method_stub(mname, params, ret, kind, used)
            lines.append(f"{indent}{stub}")

# ─── main ─────────────────────────────────────────────────────────────────────

def collect_refs(apk_paths: list[Path]) -> dict:
    """Scan all APKs in the list and return merged refs dict."""
    merged: dict = {}
    for path in apk_paths:
        print(f"  scanning {path.name} …", file=sys.stderr)
        apk_refs = scan_apk(path)
        for desc, info in apk_refs.items():
            if desc not in merged:
                merged[desc] = {"methods": defaultdict(list), "fields": {}}
            for mn, sigs in info["methods"].items():
                merged[desc]["methods"][mn].extend(sigs)
            merged[desc]["fields"].update(info["fields"])
    return merged

def plan_files(refs: dict) -> dict[str, dict]:
    """
    Group inner classes under their outer class.
    Returns { "android.view.View": { "info": {...}, "inners": {"OnClickListener": {...}} } }
    """
    plan: dict = {}

    for desc, info in refs.items():
        classname = desc_to_classname(desc)
        if "$" in classname:
            outer = classname.split("$")[0]
            inner_simple = classname.split("$")[-1]
            # Skip anonymous classes (numeric names like $1, $2)
            if inner_simple.isdigit():
                continue
            if outer not in plan:
                plan[outer] = {"info": None, "inners": {}}
            plan[outer]["inners"][inner_simple] = info
        else:
            if classname not in plan:
                plan[classname] = {"info": None, "inners": {}}
            plan[classname]["info"] = info

    return plan

def generate(apk_paths: list[Path],
             dry_run: bool = False,
             out_dir: Path | None = None,
             report: bool = False) -> None:
    out_base = out_dir or SHIMS_DIR

    print("Scanning APKs …", file=sys.stderr)
    refs = collect_refs(apk_paths)
    print(f"  {len(refs)} android/androidx class references found", file=sys.stderr)

    plan = plan_files(refs)

    generated = []
    skipped_present = []
    skipped_androidx = []

    # Classes that exist in src/java/aosp or src/generated/aidl — don't generate shims for them
    _SKIP_CLASSES = {
        "android.opengl.GLSurfaceView",
        "android.content.pm.IPackageManager",
    }

    for classname, entry in sorted(plan.items()):
        # Skip androidx.* and android.support.* — bundled inside APKs, not framework
        if classname.startswith("androidx.") or classname.startswith("android.support."):
            skipped_androidx.append(classname)
            continue
        if classname in _SKIP_CLASSES:
            skipped_present.append(classname)
            continue

        target = (out_base / classname.replace(".", "/")).with_suffix(".java")

        if target.exists():
            # Regenerate only files that we previously auto-generated
            try:
                first_line = target.read_text().split("\n", 1)[0]
            except Exception:
                first_line = ""
            if first_line != GENERATED_MARKER:
                skipped_present.append(classname)
                continue
            # fall through to regenerate

        # Generate
        src = generate_outer_class(
            classname,
            entry["info"],
            entry["inners"],
        )
        generated.append((classname, target, src))

    print(f"  {len(generated)} files to generate", file=sys.stderr)
    print(f"  {len(skipped_present)} already present", file=sys.stderr)
    print(f"  {len(skipped_androidx)} androidx (skipped)", file=sys.stderr)

    if not dry_run:
        for classname, target, src in generated:
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(src)
        print(f"Done. Wrote {len(generated)} shim files.", file=sys.stderr)
    else:
        for classname, target, src in generated[:20]:
            print(f"\n{'─'*60}")
            print(f"-- {target.relative_to(ROOT)}")
            print(src[:400])
        if len(generated) > 20:
            print(f"\n… and {len(generated)-20} more")

    if report:
        out_report_path = OUT_REPORT
        out_report_path.parent.mkdir(parents=True, exist_ok=True)
        lines = []
        lines.append(f"NovaART shim coverage report — {len(apk_paths)} APK(s) scanned")
        lines.append(f"Generated : {len(generated)}")
        lines.append(f"Present   : {len(skipped_present)}")
        lines.append(f"AndroidX  : {len(skipped_androidx)} (skipped)")
        lines.append("")
        lines.append("=== GENERATED ===")
        for cn, _, _ in sorted(generated):
            lines.append(f"  + {cn}")
        lines.append("")
        lines.append("=== ALREADY PRESENT ===")
        for cn in sorted(skipped_present):
            lines.append(f"  . {cn}")
        out_report_path.write_text("\n".join(lines))
        print(f"Report written to {out_report_path}", file=sys.stderr)

# ─── CLI ──────────────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--apks", default=None,
                        help=("Comma-separated subdirectory names under apks/ to scan "
                              "(e.g. phase2,phase3), or 'all'. "
                              "Omit to scan everything under apks/ automatically."))
    parser.add_argument("--dry-run", action="store_true",
                        help="Preview first 20 files, don't write")
    parser.add_argument("--report",  action="store_true",
                        help="Write coverage report to out/shim-coverage.txt")
    parser.add_argument("--out",     default=None,
                        help="Override output directory (default: src/java/nova-shims)")
    args = parser.parse_args()

    apk_paths = resolve_apk_paths(args.apks)
    if not apk_paths:
        print("No APK files found. Check that apks/ exists and contains .apk files.",
              file=sys.stderr)
        sys.exit(1)

    generate(
        apk_paths=apk_paths,
        dry_run=args.dry_run,
        out_dir=Path(args.out) if args.out else None,
        report=args.report,
    )

if __name__ == "__main__":
    main()
