#include "core_jni_helpers.h"

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <string.h>
#include <stdlib.h>

/* Helper: get raw bytes from a java.nio.Buffer (direct or array-backed) */
static void *get_buffer_ptr(JNIEnv *env, jobject buf, jint *out_remaining) {
    if (!buf) { if (out_remaining) *out_remaining = 0; return NULL; }
    void *ptr = (*env)->GetDirectBufferAddress(env, buf);
    if (ptr) {
        if (out_remaining) {
            jclass cls = (*env)->GetObjectClass(env, buf);
            jmethodID remaining = (*env)->GetMethodID(env, cls, "remaining", "()I");
            *out_remaining = remaining ? (*env)->CallIntMethod(env, buf, remaining) : 0;
            (*env)->DeleteLocalRef(env, cls);
        }
        return ptr;
    }
    /* Array-backed buffer: get array + position */
    jclass cls = (*env)->GetObjectClass(env, buf);
    jmethodID hasArray = (*env)->GetMethodID(env, cls, "hasArray", "()Z");
    if (hasArray && (*env)->CallBooleanMethod(env, buf, hasArray)) {
        jmethodID arrayM  = (*env)->GetMethodID(env, cls, "array", "()Ljava/lang/Object;");
        jmethodID posM    = (*env)->GetMethodID(env, cls, "position", "()I");
        jmethodID elemSzM = (*env)->GetMethodID(env, cls, "limit", "()I");
        if (arrayM && posM) {
            jobject arr = (*env)->CallObjectMethod(env, buf, arrayM);
            jint pos    = (*env)->CallIntMethod(env, buf, posM);
            if (arr) {
                /* We'll get the raw array elements — caller must release them */
                /* For simplicity, use GetPrimitiveArrayCritical */
                jsize len = (*env)->GetArrayLength(env, (jarray)arr);
                void *data = (*env)->GetPrimitiveArrayCritical(env, (jarray)arr, NULL);
                if (data) {
                    /* Determine element size from class name */
                    jclass arrCls = (*env)->GetObjectClass(env, arr);
                    jmethodID nameM = (*env)->GetMethodID(env,
                        (*env)->FindClass(env, "java/lang/Class"), "getName",
                        "()Ljava/lang/String;");
                    (void)nameM; (void)arrCls;
                    /* Assume float[] (4 bytes/elem) as default for GL usage */
                    if (out_remaining) *out_remaining = (len - pos) * 4;
                    (*env)->ReleasePrimitiveArrayCritical(env, (jarray)arr, data, JNI_ABORT);
                    (*env)->DeleteLocalRef(env, arr);
                    (*env)->DeleteLocalRef(env, cls);
                    return NULL; /* can't return raw ptr safely without pinning */
                }
                (*env)->DeleteLocalRef(env, arr);
            }
        }
    }
    (*env)->DeleteLocalRef(env, cls);
    return NULL;
}

/* Helper: get position-adjusted direct buffer pointer */
static const void *direct_buf_pos(JNIEnv *env, jobject buf) {
    if (!buf) return NULL;
    void *base = (*env)->GetDirectBufferAddress(env, buf);
    if (!base) return NULL;
    jclass cls = (*env)->GetObjectClass(env, buf);
    jmethodID posM = (*env)->GetMethodID(env, cls, "position", "()I");
    jmethodID elemSzM = cls ? (*env)->GetMethodID(env, cls, "limit", "()I") : NULL;
    (void)elemSzM;
    jint pos = posM ? (*env)->CallIntMethod(env, buf, posM) : 0;
    (*env)->DeleteLocalRef(env, cls);
    /* Determine bytes-per-element from buffer type name */
    jclass bufCls = (*env)->GetObjectClass(env, buf);
    jmethodID getClass = (*env)->GetMethodID(env,
        (*env)->FindClass(env, "java/lang/Object"), "getClass", "()Ljava/lang/Class;");
    (void)getClass;
    /* Default: assume FloatBuffer (4 bytes) for most GL usage */
    (*env)->DeleteLocalRef(env, bufCls);
    return (const char *)base + pos * 4;
}

/* Clear */
static void gl_clear(JNIEnv *e, jclass c, jint mask) { (void)e;(void)c; glClear(mask); }
static void gl_clearColor(JNIEnv *e, jclass c, jfloat r, jfloat g, jfloat b, jfloat a) { (void)e;(void)c; glClearColor(r,g,b,a); }
static void gl_clearDepthf(JNIEnv *e, jclass c, jfloat d) { (void)e;(void)c; glClearDepthf(d); }
static void gl_clearStencil(JNIEnv *e, jclass c, jint s) { (void)e;(void)c; glClearStencil(s); }

/* Viewport */
static void gl_viewport(JNIEnv *e, jclass c, jint x, jint y, jint w, jint h) { (void)e;(void)c; glViewport(x,y,w,h); }
static void gl_scissor(JNIEnv *e, jclass c, jint x, jint y, jint w, jint h) { (void)e;(void)c; glScissor(x,y,w,h); }

/* Enable/Disable */
static void gl_enable(JNIEnv *e, jclass c, jint cap) { (void)e;(void)c; glEnable(cap); }
static void gl_disable(JNIEnv *e, jclass c, jint cap) { (void)e;(void)c; glDisable(cap); }
static void gl_depthMask(JNIEnv *e, jclass c, jboolean flag) { (void)e;(void)c; glDepthMask(flag ? GL_TRUE : GL_FALSE); }
static void gl_colorMask(JNIEnv *e, jclass c, jboolean r, jboolean g, jboolean b, jboolean a) {
    (void)e;(void)c; glColorMask(r,g,b,a);
}

/* Blend */
static void gl_blendFunc(JNIEnv *e, jclass c, jint sf, jint df) { (void)e;(void)c; glBlendFunc(sf,df); }
static void gl_blendFuncSeparate(JNIEnv *e, jclass c, jint sr, jint dr, jint sa, jint da) { (void)e;(void)c; glBlendFuncSeparate(sr,dr,sa,da); }
static void gl_blendEquation(JNIEnv *e, jclass c, jint mode) { (void)e;(void)c; glBlendEquation(mode); }
static void gl_blendColor(JNIEnv *e, jclass c, jfloat r, jfloat g, jfloat b, jfloat a) { (void)e;(void)c; glBlendColor(r,g,b,a); }

/* Depth */
static void gl_depthFunc(JNIEnv *e, jclass c, jint func) { (void)e;(void)c; glDepthFunc(func); }
static void gl_depthRangef(JNIEnv *e, jclass c, jfloat n, jfloat f) { (void)e;(void)c; glDepthRangef(n,f); }

/* Cull */
static void gl_cullFace(JNIEnv *e, jclass c, jint mode) { (void)e;(void)c; glCullFace(mode); }
static void gl_frontFace(JNIEnv *e, jclass c, jint mode) { (void)e;(void)c; glFrontFace(mode); }

/* Error */
static jint gl_getError(JNIEnv *e, jclass c) { (void)e;(void)c; return (jint)glGetError(); }

/* Shaders */
static jint gl_createShader(JNIEnv *e, jclass c, jint type) { (void)e;(void)c; return (jint)glCreateShader(type); }
static void gl_shaderSource(JNIEnv *e, jclass c, jint shader, jstring str) {
    (void)c;
    const char *src = (*e)->GetStringUTFChars(e, str, NULL);
    glShaderSource(shader, 1, &src, NULL);
    (*e)->ReleaseStringUTFChars(e, str, src);
}
static void gl_compileShader(JNIEnv *e, jclass c, jint s) { (void)e;(void)c; glCompileShader(s); }
static void gl_deleteShader(JNIEnv *e, jclass c, jint s) { (void)e;(void)c; glDeleteShader(s); }
static void gl_getShaderiv(JNIEnv *e, jclass c, jint s, jint pname, jintArray params, jint off) {
    (void)c;
    jint *arr = (*e)->GetIntArrayElements(e, params, NULL);
    glGetShaderiv(s, pname, (GLint*)(arr + off));
    (*e)->ReleaseIntArrayElements(e, params, arr, 0);
}
static jstring gl_getShaderInfoLog(JNIEnv *e, jclass c, jint s) {
    (void)c;
    GLint len = 0;
    glGetShaderiv(s, GL_INFO_LOG_LENGTH, &len);
    if (len <= 0) return (*e)->NewStringUTF(e, "");
    char *buf = (char *)malloc(len + 1);
    glGetShaderInfoLog(s, len, NULL, buf);
    jstring result = (*e)->NewStringUTF(e, buf);
    free(buf);
    return result;
}
static jboolean gl_isShader(JNIEnv *e, jclass c, jint s) { (void)e;(void)c; return (jboolean)glIsShader(s); }

/* Programs */
static jint  gl_createProgram(JNIEnv *e, jclass c) { (void)e;(void)c; return (jint)glCreateProgram(); }
static void gl_attachShader(JNIEnv *e, jclass c, jint prog, jint s) { (void)e;(void)c; glAttachShader(prog,s); }
static void gl_detachShader(JNIEnv *e, jclass c, jint prog, jint s) { (void)e;(void)c; glDetachShader(prog,s); }
static void gl_linkProgram(JNIEnv *e, jclass c, jint p) { (void)e;(void)c; glLinkProgram(p); }
static void gl_useProgram(JNIEnv *e, jclass c, jint p) { (void)e;(void)c; glUseProgram(p); }
static void gl_deleteProgram(JNIEnv *e, jclass c, jint p) { (void)e;(void)c; glDeleteProgram(p); }
static void gl_validateProgram(JNIEnv *e, jclass c, jint p) { (void)e;(void)c; glValidateProgram(p); }
static void gl_getProgramiv(JNIEnv *e, jclass c, jint p, jint pname, jintArray params, jint off) {
    (void)c;
    jint *arr = (*e)->GetIntArrayElements(e, params, NULL);
    glGetProgramiv(p, pname, (GLint*)(arr + off));
    (*e)->ReleaseIntArrayElements(e, params, arr, 0);
}
static jstring gl_getProgramInfoLog(JNIEnv *e, jclass c, jint p) {
    (void)c;
    GLint len = 0;
    glGetProgramiv(p, GL_INFO_LOG_LENGTH, &len);
    if (len <= 0) return (*e)->NewStringUTF(e, "");
    char *buf = (char *)malloc(len + 1);
    glGetProgramInfoLog(p, len, NULL, buf);
    jstring r = (*e)->NewStringUTF(e, buf);
    free(buf);
    return r;
}
static jboolean gl_isProgram(JNIEnv *e, jclass c, jint p) { (void)e;(void)c; return (jboolean)glIsProgram(p); }

/* Attributes */
static jint gl_getAttribLocation(JNIEnv *e, jclass c, jint p, jstring name) {
    (void)c;
    const char *n = (*e)->GetStringUTFChars(e, name, NULL);
    jint r = glGetAttribLocation(p, n);
    (*e)->ReleaseStringUTFChars(e, name, n);
    return r;
}
static void gl_bindAttribLocation(JNIEnv *e, jclass c, jint p, jint idx, jstring name) {
    (void)c;
    const char *n = (*e)->GetStringUTFChars(e, name, NULL);
    glBindAttribLocation(p, idx, n);
    (*e)->ReleaseStringUTFChars(e, name, n);
}
static void gl_enableVertexAttribArray(JNIEnv *e, jclass c, jint idx) { (void)e;(void)c; glEnableVertexAttribArray(idx); }
static void gl_disableVertexAttribArray(JNIEnv *e, jclass c, jint idx) { (void)e;(void)c; glDisableVertexAttribArray(idx); }
static void gl_vertexAttribPointerBuf(JNIEnv *e, jclass c, jint idx, jint size, jint type,
        jboolean norm, jint stride, jobject buf) {
    (void)c;
    const void *ptr = buf ? direct_buf_pos(e, buf) : NULL;
    glVertexAttribPointer(idx, size, type, norm, stride, ptr);
}
static void gl_vertexAttribPointerOff(JNIEnv *e, jclass c, jint idx, jint size, jint type,
        jboolean norm, jint stride, jint offset) {
    (void)e;(void)c; glVertexAttribPointer(idx, size, type, norm, stride, (const void*)(intptr_t)offset);
}
static void gl_vertexAttrib1f(JNIEnv *e, jclass c, jint i, jfloat x) { (void)e;(void)c; glVertexAttrib1f(i,x); }
static void gl_vertexAttrib2f(JNIEnv *e, jclass c, jint i, jfloat x, jfloat y) { (void)e;(void)c; glVertexAttrib2f(i,x,y); }
static void gl_vertexAttrib3f(JNIEnv *e, jclass c, jint i, jfloat x, jfloat y, jfloat z) { (void)e;(void)c; glVertexAttrib3f(i,x,y,z); }
static void gl_vertexAttrib4f(JNIEnv *e, jclass c, jint i, jfloat x, jfloat y, jfloat z, jfloat w) { (void)e;(void)c; glVertexAttrib4f(i,x,y,z,w); }
static void gl_vertexAttrib1fv(JNIEnv *e, jclass c, jint i, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glVertexAttrib1fv(i, (const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_vertexAttrib2fv(JNIEnv *e, jclass c, jint i, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glVertexAttrib2fv(i, (const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_vertexAttrib3fv(JNIEnv *e, jclass c, jint i, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glVertexAttrib3fv(i, (const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_vertexAttrib4fv(JNIEnv *e, jclass c, jint i, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glVertexAttrib4fv(i, (const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}

/* Uniforms */
static jint gl_getUniformLocation(JNIEnv *e, jclass c, jint p, jstring name) {
    (void)c;
    const char *n = (*e)->GetStringUTFChars(e, name, NULL);
    jint r = glGetUniformLocation(p, n);
    (*e)->ReleaseStringUTFChars(e, name, n);
    return r;
}
static void gl_uniform1i(JNIEnv *e, jclass c, jint loc, jint x) { (void)e;(void)c; glUniform1i(loc,x); }
static void gl_uniform2i(JNIEnv *e, jclass c, jint loc, jint x, jint y) { (void)e;(void)c; glUniform2i(loc,x,y); }
static void gl_uniform3i(JNIEnv *e, jclass c, jint loc, jint x, jint y, jint z) { (void)e;(void)c; glUniform3i(loc,x,y,z); }
static void gl_uniform4i(JNIEnv *e, jclass c, jint loc, jint x, jint y, jint z, jint w) { (void)e;(void)c; glUniform4i(loc,x,y,z,w); }
static void gl_uniform1f(JNIEnv *e, jclass c, jint loc, jfloat x) { (void)e;(void)c; glUniform1f(loc,x); }
static void gl_uniform2f(JNIEnv *e, jclass c, jint loc, jfloat x, jfloat y) { (void)e;(void)c; glUniform2f(loc,x,y); }
static void gl_uniform3f(JNIEnv *e, jclass c, jint loc, jfloat x, jfloat y, jfloat z) { (void)e;(void)c; glUniform3f(loc,x,y,z); }
static void gl_uniform4f(JNIEnv *e, jclass c, jint loc, jfloat x, jfloat y, jfloat z, jfloat w) { (void)e;(void)c; glUniform4f(loc,x,y,z,w); }
static void gl_uniform1fv(JNIEnv *e, jclass c, jint loc, jint cnt, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniform1fv(loc,cnt,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniform2fv(JNIEnv *e, jclass c, jint loc, jint cnt, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniform2fv(loc,cnt,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniform3fv(JNIEnv *e, jclass c, jint loc, jint cnt, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniform3fv(loc,cnt,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniform4fv(JNIEnv *e, jclass c, jint loc, jint cnt, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniform4fv(loc,cnt,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniform1iv(JNIEnv *e, jclass c, jint loc, jint cnt, jintArray v, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,v,NULL);
    glUniform1iv(loc,cnt,(const GLint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniformMatrix2fv(JNIEnv *e, jclass c, jint loc, jint cnt, jboolean t, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniformMatrix2fv(loc,cnt,t,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniformMatrix3fv(JNIEnv *e, jclass c, jint loc, jint cnt, jboolean t, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniformMatrix3fv(loc,cnt,t,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}
static void gl_uniformMatrix4fv(JNIEnv *e, jclass c, jint loc, jint cnt, jboolean t, jfloatArray v, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,v,NULL);
    glUniformMatrix4fv(loc,cnt,t,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,v,arr,JNI_ABORT);
}

/* Textures */
static void gl_genTextures(JNIEnv *e, jclass c, jint n, jintArray texs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,texs,NULL);
    glGenTextures(n,(GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,texs,arr,0);
}
static void gl_deleteTextures(JNIEnv *e, jclass c, jint n, jintArray texs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,texs,NULL);
    glDeleteTextures(n,(const GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,texs,arr,JNI_ABORT);
}
static void gl_bindTexture(JNIEnv *e, jclass c, jint tgt, jint tex) { (void)e;(void)c; glBindTexture(tgt,tex); }
static void gl_activeTexture(JNIEnv *e, jclass c, jint tex) { (void)e;(void)c; glActiveTexture(tex); }
static void gl_texImage2D(JNIEnv *e, jclass c, jint tgt, jint lvl, jint ifmt,
        jint w, jint h, jint bdr, jint fmt, jint type, jobject pixels) {
    (void)c;
    const void *ptr = pixels ? direct_buf_pos(e, pixels) : NULL;
    glTexImage2D(tgt, lvl, ifmt, w, h, bdr, fmt, type, ptr);
}
static void gl_texSubImage2D(JNIEnv *e, jclass c, jint tgt, jint lvl, jint xo, jint yo,
        jint w, jint h, jint fmt, jint type, jobject pixels) {
    (void)c;
    const void *ptr = pixels ? direct_buf_pos(e, pixels) : NULL;
    glTexSubImage2D(tgt, lvl, xo, yo, w, h, fmt, type, ptr);
}
static void gl_texParameteri(JNIEnv *e, jclass c, jint tgt, jint pname, jint param) { (void)e;(void)c; glTexParameteri(tgt,pname,param); }
static void gl_texParameterf(JNIEnv *e, jclass c, jint tgt, jint pname, jfloat param) { (void)e;(void)c; glTexParameterf(tgt,pname,param); }
static void gl_texParameterfv(JNIEnv *e, jclass c, jint tgt, jint pname, jfloatArray p, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,p,NULL);
    glTexParameterfv(tgt,pname,(const GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,p,arr,JNI_ABORT);
}
static void gl_texParameteriv(JNIEnv *e, jclass c, jint tgt, jint pname, jintArray p, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,p,NULL);
    glTexParameteriv(tgt,pname,(const GLint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,p,arr,JNI_ABORT);
}
static void gl_generateMipmap(JNIEnv *e, jclass c, jint tgt) { (void)e;(void)c; glGenerateMipmap(tgt); }
static jboolean gl_isTexture(JNIEnv *e, jclass c, jint t) { (void)e;(void)c; return (jboolean)glIsTexture(t); }
static jint gl_getTexParameteriv_single(JNIEnv *e, jclass c, jint tgt, jint pname) {
    (void)e;(void)c; GLint v=0; glGetTexParameteriv(tgt,pname,&v); return v;
}

/* Draw */
static void gl_drawArrays(JNIEnv *e, jclass c, jint mode, jint first, jint cnt) { (void)e;(void)c; glDrawArrays(mode,first,cnt); }
static void gl_drawElementsBuf(JNIEnv *e, jclass c, jint mode, jint cnt, jint type, jobject idx) {
    (void)c;
    const void *ptr = idx ? direct_buf_pos(e, idx) : NULL;
    glDrawElements(mode, cnt, type, ptr);
}
static void gl_drawElementsOff(JNIEnv *e, jclass c, jint mode, jint cnt, jint type, jint offset) {
    (void)e;(void)c; glDrawElements(mode, cnt, type, (const void*)(intptr_t)offset);
}
static void gl_lineWidth(JNIEnv *e, jclass c, jfloat w) { (void)e;(void)c; glLineWidth(w); }
static void gl_pointSize(JNIEnv *e, jclass c, jfloat s) { (void)e;(void)c; /* no glPointSize in GLES20 */ (void)s; }

/* Buffers */
static void gl_genBuffers(JNIEnv *e, jclass c, jint n, jintArray bufs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,bufs,NULL);
    glGenBuffers(n,(GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,bufs,arr,0);
}
static void gl_deleteBuffers(JNIEnv *e, jclass c, jint n, jintArray bufs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,bufs,NULL);
    glDeleteBuffers(n,(const GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,bufs,arr,JNI_ABORT);
}
static void gl_bindBuffer(JNIEnv *e, jclass c, jint tgt, jint buf) { (void)e;(void)c; glBindBuffer(tgt,buf); }
static void gl_bufferData(JNIEnv *e, jclass c, jint tgt, jint size, jobject data, jint usage) {
    (void)c;
    const void *ptr = data ? direct_buf_pos(e, data) : NULL;
    glBufferData(tgt, size, ptr, usage);
}
static void gl_bufferSubData(JNIEnv *e, jclass c, jint tgt, jint off, jint size, jobject data) {
    (void)c;
    const void *ptr = data ? direct_buf_pos(e, data) : NULL;
    glBufferSubData(tgt, off, size, ptr);
}
static jboolean gl_isBuffer(JNIEnv *e, jclass c, jint b) { (void)e;(void)c; return (jboolean)glIsBuffer(b); }

/* Framebuffers */
static void gl_genFramebuffers(JNIEnv *e, jclass c, jint n, jintArray fbs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,fbs,NULL);
    glGenFramebuffers(n,(GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,fbs,arr,0);
}
static void gl_deleteFramebuffers(JNIEnv *e, jclass c, jint n, jintArray fbs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,fbs,NULL);
    glDeleteFramebuffers(n,(const GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,fbs,arr,JNI_ABORT);
}
static void gl_bindFramebuffer(JNIEnv *e, jclass c, jint tgt, jint fb) { (void)e;(void)c; glBindFramebuffer(tgt,fb); }
static void gl_framebufferTexture2D(JNIEnv *e, jclass c, jint tgt, jint att, jint ttgt, jint tex, jint lvl) {
    (void)e;(void)c; glFramebufferTexture2D(tgt,att,ttgt,tex,lvl);
}
static void gl_framebufferRenderbuffer(JNIEnv *e, jclass c, jint tgt, jint att, jint rbt, jint rb) {
    (void)e;(void)c; glFramebufferRenderbuffer(tgt,att,rbt,rb);
}
static jint gl_checkFramebufferStatus(JNIEnv *e, jclass c, jint tgt) { (void)e;(void)c; return (jint)glCheckFramebufferStatus(tgt); }

/* Renderbuffers */
static void gl_genRenderbuffers(JNIEnv *e, jclass c, jint n, jintArray rbs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,rbs,NULL);
    glGenRenderbuffers(n,(GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,rbs,arr,0);
}
static void gl_deleteRenderbuffers(JNIEnv *e, jclass c, jint n, jintArray rbs, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,rbs,NULL);
    glDeleteRenderbuffers(n,(const GLuint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,rbs,arr,JNI_ABORT);
}
static void gl_bindRenderbuffer(JNIEnv *e, jclass c, jint tgt, jint rb) { (void)e;(void)c; glBindRenderbuffer(tgt,rb); }
static void gl_renderbufferStorage(JNIEnv *e, jclass c, jint tgt, jint ifmt, jint w, jint h) {
    (void)e;(void)c; glRenderbufferStorage(tgt,ifmt,w,h);
}

/* Misc */
static void gl_flush(JNIEnv *e, jclass c) { (void)e;(void)c; glFlush(); }
static void gl_finish(JNIEnv *e, jclass c) { (void)e;(void)c; glFinish(); }
static jstring gl_getString(JNIEnv *e, jclass c, jint name) {
    (void)c;
    const char *s = (const char *)glGetString(name);
    return (*e)->NewStringUTF(e, s ? s : "");
}
static void gl_getIntegerv(JNIEnv *e, jclass c, jint pname, jintArray params, jint off) {
    (void)c; jint *arr = (*e)->GetIntArrayElements(e,params,NULL);
    glGetIntegerv(pname,(GLint*)(arr+off));
    (*e)->ReleaseIntArrayElements(e,params,arr,0);
}
static void gl_getFloatv(JNIEnv *e, jclass c, jint pname, jfloatArray params, jint off) {
    (void)c; jfloat *arr = (*e)->GetFloatArrayElements(e,params,NULL);
    glGetFloatv(pname,(GLfloat*)(arr+off));
    (*e)->ReleaseFloatArrayElements(e,params,arr,0);
}
static void gl_getBooleanv(JNIEnv *e, jclass c, jint pname, jbooleanArray params, jint off) {
    (void)c; jboolean *arr = (*e)->GetBooleanArrayElements(e,params,NULL);
    GLboolean tmp = 0; glGetBooleanv(pname,&tmp); arr[off] = (jboolean)tmp;
    (*e)->ReleaseBooleanArrayElements(e,params,arr,0);
}
static void gl_polygonOffset(JNIEnv *e, jclass c, jfloat f, jfloat u) { (void)e;(void)c; glPolygonOffset(f,u); }
static void gl_sampleCoverage(JNIEnv *e, jclass c, jfloat v, jboolean inv) { (void)e;(void)c; glSampleCoverage(v,inv); }
static void gl_stencilFunc(JNIEnv *e, jclass c, jint func, jint ref, jint mask) { (void)e;(void)c; glStencilFunc(func,ref,mask); }
static void gl_stencilMask(JNIEnv *e, jclass c, jint mask) { (void)e;(void)c; glStencilMask(mask); }
static void gl_stencilOp(JNIEnv *e, jclass c, jint fail, jint zfail, jint zpass) { (void)e;(void)c; glStencilOp(fail,zfail,zpass); }
static void gl_readPixels(JNIEnv *e, jclass c, jint x, jint y, jint w, jint h,
        jint fmt, jint type, jobject pixels) {
    (void)c;
    void *ptr = pixels ? direct_buf_pos(e, pixels) : NULL;
    if (ptr) glReadPixels(x,y,w,h,fmt,type,ptr);
}
static void gl_pixelStorei(JNIEnv *e, jclass c, jint pname, jint param) { (void)e;(void)c; glPixelStorei(pname,param); }
static void gl_copyTexImage2D(JNIEnv *e, jclass c, jint tgt, jint lvl, jint ifmt, jint x, jint y, jint w, jint h, jint bdr) {
    (void)e;(void)c; glCopyTexImage2D(tgt,lvl,ifmt,x,y,w,h,bdr);
}
static void gl_copyTexSubImage2D(JNIEnv *e, jclass c, jint tgt, jint lvl, jint xo, jint yo, jint x, jint y, jint w, jint h) {
    (void)e;(void)c; glCopyTexSubImage2D(tgt,lvl,xo,yo,x,y,w,h);
}
static jint gl_getUniformBlockIndex(JNIEnv *e, jclass c, jint prog, jstring name) {
    (void)e;(void)c;(void)name; return 0;
}
static void gl_uniformBlockBinding(JNIEnv *e, jclass c, jint prog, jint idx, jint binding) {
    (void)e;(void)c;(void)prog;(void)idx;(void)binding;
}
static void gl_genVertexArraysOES(JNIEnv *e, jclass c, jint n, jintArray arrays, jint off) {
    (void)e;(void)c;(void)n;(void)arrays;(void)off;
}
static void gl_bindVertexArrayOES(JNIEnv *e, jclass c, jint array) { (void)e;(void)c;(void)array; }
static void gl_deleteVertexArraysOES(JNIEnv *e, jclass c, jint n, jintArray arrays, jint off) {
    (void)e;(void)c;(void)n;(void)arrays;(void)off;
}

static const JNINativeMethod gMethods[] = {
    { "glClear",                "(I)V",       (void*)gl_clear },
    { "glClearColor",           "(FFFF)V",    (void*)gl_clearColor },
    { "glClearDepthf",          "(F)V",       (void*)gl_clearDepthf },
    { "glClearStencil",         "(I)V",       (void*)gl_clearStencil },
    { "glViewport",             "(IIII)V",    (void*)gl_viewport },
    { "glScissor",              "(IIII)V",    (void*)gl_scissor },
    { "glEnable",               "(I)V",       (void*)gl_enable },
    { "glDisable",              "(I)V",       (void*)gl_disable },
    { "glDepthMask",            "(Z)V",       (void*)gl_depthMask },
    { "glColorMask",            "(ZZZZ)V",    (void*)gl_colorMask },
    { "glBlendFunc",            "(II)V",      (void*)gl_blendFunc },
    { "glBlendFuncSeparate",    "(IIII)V",    (void*)gl_blendFuncSeparate },
    { "glBlendEquation",        "(I)V",       (void*)gl_blendEquation },
    { "glBlendColor",           "(FFFF)V",    (void*)gl_blendColor },
    { "glDepthFunc",            "(I)V",       (void*)gl_depthFunc },
    { "glDepthRangef",          "(FF)V",      (void*)gl_depthRangef },
    { "glCullFace",             "(I)V",       (void*)gl_cullFace },
    { "glFrontFace",            "(I)V",       (void*)gl_frontFace },
    { "glGetError",             "()I",        (void*)gl_getError },
    { "glCreateShader",         "(I)I",       (void*)gl_createShader },
    { "glShaderSource",         "(ILjava/lang/String;)V", (void*)gl_shaderSource },
    { "glCompileShader",        "(I)V",       (void*)gl_compileShader },
    { "glDeleteShader",         "(I)V",       (void*)gl_deleteShader },
    { "glGetShaderiv",          "(II[II)V",   (void*)gl_getShaderiv },
    { "glGetShaderInfoLog",     "(I)Ljava/lang/String;", (void*)gl_getShaderInfoLog },
    { "glIsShader",             "(I)Z",       (void*)gl_isShader },
    { "glCreateProgram",        "()I",        (void*)gl_createProgram },
    { "glAttachShader",         "(II)V",      (void*)gl_attachShader },
    { "glDetachShader",         "(II)V",      (void*)gl_detachShader },
    { "glLinkProgram",          "(I)V",       (void*)gl_linkProgram },
    { "glUseProgram",           "(I)V",       (void*)gl_useProgram },
    { "glDeleteProgram",        "(I)V",       (void*)gl_deleteProgram },
    { "glValidateProgram",      "(I)V",       (void*)gl_validateProgram },
    { "glGetProgramiv",         "(II[II)V",   (void*)gl_getProgramiv },
    { "glGetProgramInfoLog",    "(I)Ljava/lang/String;", (void*)gl_getProgramInfoLog },
    { "glIsProgram",            "(I)Z",       (void*)gl_isProgram },
    { "glGetAttribLocation",    "(ILjava/lang/String;)I", (void*)gl_getAttribLocation },
    { "glBindAttribLocation",   "(IILjava/lang/String;)V", (void*)gl_bindAttribLocation },
    { "glEnableVertexAttribArray",  "(I)V",   (void*)gl_enableVertexAttribArray },
    { "glDisableVertexAttribArray", "(I)V",   (void*)gl_disableVertexAttribArray },
    { "glVertexAttribPointer",  "(IIIZILjava/nio/Buffer;)V", (void*)gl_vertexAttribPointerBuf },
    { "glVertexAttribPointer",  "(IIIZII)V",  (void*)gl_vertexAttribPointerOff },
    { "glVertexAttrib1f",       "(IF)V",      (void*)gl_vertexAttrib1f },
    { "glVertexAttrib2f",       "(IFF)V",     (void*)gl_vertexAttrib2f },
    { "glVertexAttrib3f",       "(IFFF)V",    (void*)gl_vertexAttrib3f },
    { "glVertexAttrib4f",       "(IFFFF)V",   (void*)gl_vertexAttrib4f },
    { "glVertexAttrib1fv",      "(I[FI)V",    (void*)gl_vertexAttrib1fv },
    { "glVertexAttrib2fv",      "(I[FI)V",    (void*)gl_vertexAttrib2fv },
    { "glVertexAttrib3fv",      "(I[FI)V",    (void*)gl_vertexAttrib3fv },
    { "glVertexAttrib4fv",      "(I[FI)V",    (void*)gl_vertexAttrib4fv },
    { "glGetUniformLocation",   "(ILjava/lang/String;)I", (void*)gl_getUniformLocation },
    { "glUniform1i",            "(II)V",      (void*)gl_uniform1i },
    { "glUniform2i",            "(III)V",     (void*)gl_uniform2i },
    { "glUniform3i",            "(IIII)V",    (void*)gl_uniform3i },
    { "glUniform4i",            "(IIIII)V",   (void*)gl_uniform4i },
    { "glUniform1f",            "(IF)V",      (void*)gl_uniform1f },
    { "glUniform2f",            "(IFF)V",     (void*)gl_uniform2f },
    { "glUniform3f",            "(IFFF)V",    (void*)gl_uniform3f },
    { "glUniform4f",            "(IFFFF)V",   (void*)gl_uniform4f },
    { "glUniform1fv",           "(II[FI)V",   (void*)gl_uniform1fv },
    { "glUniform2fv",           "(II[FI)V",   (void*)gl_uniform2fv },
    { "glUniform3fv",           "(II[FI)V",   (void*)gl_uniform3fv },
    { "glUniform4fv",           "(II[FI)V",   (void*)gl_uniform4fv },
    { "glUniform1iv",           "(II[II)V",   (void*)gl_uniform1iv },
    { "glUniformMatrix2fv",     "(IIZ[FI)V",  (void*)gl_uniformMatrix2fv },
    { "glUniformMatrix3fv",     "(IIZ[FI)V",  (void*)gl_uniformMatrix3fv },
    { "glUniformMatrix4fv",     "(IIZ[FI)V",  (void*)gl_uniformMatrix4fv },
    { "glGenTextures",          "(I[II)V",    (void*)gl_genTextures },
    { "glDeleteTextures",       "(I[II)V",    (void*)gl_deleteTextures },
    { "glBindTexture",          "(II)V",      (void*)gl_bindTexture },
    { "glActiveTexture",        "(I)V",       (void*)gl_activeTexture },
    { "glTexImage2D",           "(IIIIIIIILjava/nio/Buffer;)V", (void*)gl_texImage2D },
    { "glTexSubImage2D",        "(IIIIIIIILjava/nio/Buffer;)V", (void*)gl_texSubImage2D },
    { "glTexParameteri",        "(III)V",     (void*)gl_texParameteri },
    { "glTexParameterf",        "(IIF)V",     (void*)gl_texParameterf },
    { "glTexParameterfv",       "(II[FI)V",   (void*)gl_texParameterfv },
    { "glTexParameteriv",       "(II[II)V",   (void*)gl_texParameteriv },
    { "glGenerateMipmap",       "(I)V",       (void*)gl_generateMipmap },
    { "glIsTexture",            "(I)Z",       (void*)gl_isTexture },
    { "glGetTexParameteriv_single", "(II)I",  (void*)gl_getTexParameteriv_single },
    { "glDrawArrays",           "(III)V",     (void*)gl_drawArrays },
    { "glDrawElements",         "(IIILjava/nio/Buffer;)V", (void*)gl_drawElementsBuf },
    { "glDrawElements",         "(IIII)V",    (void*)gl_drawElementsOff },
    { "glLineWidth",            "(F)V",       (void*)gl_lineWidth },
    { "glPointSize",            "(F)V",       (void*)gl_pointSize },
    { "glGenBuffers",           "(I[II)V",    (void*)gl_genBuffers },
    { "glDeleteBuffers",        "(I[II)V",    (void*)gl_deleteBuffers },
    { "glBindBuffer",           "(II)V",      (void*)gl_bindBuffer },
    { "glBufferData",           "(IILjava/nio/Buffer;I)V", (void*)gl_bufferData },
    { "glBufferSubData",        "(IIILjava/nio/Buffer;)V", (void*)gl_bufferSubData },
    { "glIsBuffer",             "(I)Z",       (void*)gl_isBuffer },
    { "glGenFramebuffers",      "(I[II)V",    (void*)gl_genFramebuffers },
    { "glDeleteFramebuffers",   "(I[II)V",    (void*)gl_deleteFramebuffers },
    { "glBindFramebuffer",      "(II)V",      (void*)gl_bindFramebuffer },
    { "glFramebufferTexture2D", "(IIIII)V",   (void*)gl_framebufferTexture2D },
    { "glFramebufferRenderbuffer","(IIII)V",  (void*)gl_framebufferRenderbuffer },
    { "glCheckFramebufferStatus","(I)I",      (void*)gl_checkFramebufferStatus },
    { "glGenRenderbuffers",     "(I[II)V",    (void*)gl_genRenderbuffers },
    { "glDeleteRenderbuffers",  "(I[II)V",    (void*)gl_deleteRenderbuffers },
    { "glBindRenderbuffer",     "(II)V",      (void*)gl_bindRenderbuffer },
    { "glRenderbufferStorage",  "(IIII)V",    (void*)gl_renderbufferStorage },
    { "glFlush",                "()V",        (void*)gl_flush },
    { "glFinish",               "()V",        (void*)gl_finish },
    { "glGetString",            "(I)Ljava/lang/String;", (void*)gl_getString },
    { "glGetIntegerv",          "(I[II)V",    (void*)gl_getIntegerv },
    { "glGetFloatv",            "(I[FI)V",    (void*)gl_getFloatv },
    { "glGetBooleanv",          "(I[ZI)V",    (void*)gl_getBooleanv },
    { "glPolygonOffset",        "(FF)V",      (void*)gl_polygonOffset },
    { "glSampleCoverage",       "(FZ)V",      (void*)gl_sampleCoverage },
    { "glStencilFunc",          "(III)V",     (void*)gl_stencilFunc },
    { "glStencilMask",          "(I)V",       (void*)gl_stencilMask },
    { "glStencilOp",            "(III)V",     (void*)gl_stencilOp },
    { "glReadPixels",           "(IIIIIILjava/nio/Buffer;)V", (void*)gl_readPixels },
    { "glPixelStorei",          "(II)V",      (void*)gl_pixelStorei },
    { "glCopyTexImage2D",       "(IIIIIIII)V",(void*)gl_copyTexImage2D },
    { "glCopyTexSubImage2D",    "(IIIIIIII)V",(void*)gl_copyTexSubImage2D },
    { "glGetUniformBlockIndex", "(ILjava/lang/String;)I", (void*)gl_getUniformBlockIndex },
    { "glUniformBlockBinding",  "(III)V",     (void*)gl_uniformBlockBinding },
    { "glGenVertexArraysOES",   "(I[II)V",    (void*)gl_genVertexArraysOES },
    { "glBindVertexArrayOES",   "(I)V",       (void*)gl_bindVertexArrayOES },
    { "glDeleteVertexArraysOES","(I[II)V",    (void*)gl_deleteVertexArraysOES },
};

int register_android_opengl_GLES20(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/opengl/GLES20",
                                gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
