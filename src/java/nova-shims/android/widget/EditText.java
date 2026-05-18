package android.widget;

public class EditText {
    public void addTextChangedListener(android.text.TextWatcher p0) {}
    public void autofill(android.view.autofill.AutofillValue p0) {}
    public void clearFocus() {}
    public void drawableStateChanged() {}
    public android.graphics.drawable.Drawable getBackground() { return null; }
    public int getBaseline() { return 0; }
    public android.graphics.drawable.Drawable[] getCompoundDrawablesRelative() { return null; }
    public int getCompoundPaddingBottom() { return 0; }
    public int getCompoundPaddingLeft() { return 0; }
    public int getCompoundPaddingRight() { return 0; }
    public int getCompoundPaddingTop() { return 0; }
    public android.content.Context getContext() { return null; }
    public android.view.ActionMode.Callback getCustomSelectionActionModeCallback() { return null; }
    public android.text.Editable getEditableText() { return null; }
    public android.text.InputFilter[] getFilters() { return null; }
    public void getFocusedRect(android.graphics.Rect p0) {}
    public boolean getGlobalVisibleRect(android.graphics.Rect p0, android.graphics.Point p1) { return false; }
    public int getGravity() { return 0; }
    public int getHeight() { return 0; }
    public java.lang.CharSequence getHint() { return null; }
    public android.content.res.ColorStateList getHintTextColors() { return null; }
    public int getId() { return 0; }
    public int getInputType() { return 0; }
    public android.text.method.KeyListener getKeyListener() { return null; }
    public float getLetterSpacing() { return 0f; }
    public int getLineCount() { return 0; }
    public int getMeasuredHeight() { return 0; }
    public int getMeasuredWidth() { return 0; }
    public int getMinLines() { return 0; }
    public int getMinimumHeight() { return 0; }
    public android.view.View.OnFocusChangeListener getOnFocusChangeListener() { return null; }
    public int getPaddingBottom() { return 0; }
    public int getPaddingEnd() { return 0; }
    public int getPaddingLeft() { return 0; }
    public int getPaddingRight() { return 0; }
    public int getPaddingStart() { return 0; }
    public int getPaddingTop() { return 0; }
    public android.view.ViewParent getParent() { return null; }
    public android.content.res.Resources getResources() { return null; }
    public int getSelectionEnd() { return 0; }
    public int getSelectionStart() { return 0; }
    public android.text.Editable getText() { return null; }
    public android.view.textclassifier.TextClassifier getTextClassifier() { return null; }
    public android.graphics.drawable.Drawable getTextCursorDrawable() { return null; }
    public float getTextSize() { return 0f; }
    public android.text.method.TransformationMethod getTransformationMethod() { return null; }
    public android.graphics.Typeface getTypeface() { return null; }
    public int getWidth() { return 0; }
    public android.os.IBinder getWindowToken() { return null; }
    public boolean hasFocus() { return false; }
    public boolean isAttachedToWindow() { return false; }
    public boolean isClickable() { return false; }
    public boolean isEnabled() { return false; }
    public boolean isFocusable() { return false; }
    public boolean isFocused() { return false; }
    public boolean isHovered() { return false; }
    public boolean isInEditMode() { return false; }
    public boolean isLongClickable() { return false; }
    public int length() { return 0; }
    public void onAttachedToWindow() {}
    public android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo p0) { return null; }
    public void onDetachedFromWindow() {}
    public boolean onDragEvent(android.view.DragEvent p0) { return false; }
    public void onDraw(android.graphics.Canvas p0) {}
    public void onEditorAction(int p0) {}
    public void onFinishInflate() {}
    public void onFocusChanged(boolean p0, int p1, android.graphics.Rect p2) {}
    public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo p0) {}
    public boolean onKeyPreIme(int p0, android.view.KeyEvent p1) { return false; }
    public void onLayout(boolean p0, int p1, int p2, int p3, int p4) {}
    public android.view.ContentInfo onReceiveContent(android.view.ContentInfo p0) { return null; }
    public void onSelectionChanged(int p0, int p1) {}
    public boolean onTextContextMenuItem(int p0) { return false; }
    public boolean post(java.lang.Runnable p0) { return false; }
    public boolean postDelayed(java.lang.Runnable p0, long p1) { return false; }
    public void refreshDrawableState() {}
    public boolean removeCallbacks(java.lang.Runnable p0) { return false; }
    public void removeTextChangedListener(android.text.TextWatcher p0) {}
    public boolean requestFocus() { return false; }
    public boolean requestFocusFromTouch() { return false; }
    public void requestLayout() {}
    public boolean requestRectangleOnScreen(android.graphics.Rect p0) { return false; }
    public void selectAll() {}
    public void sendAccessibilityEvent(int p0) {}
    public void setAccessibilityDelegate(android.view.View.AccessibilityDelegate p0) {}
    public void setAlpha(float p0) {}
    public void setBackground(android.graphics.drawable.Drawable p0) {}
    public void setBackgroundDrawable(android.graphics.drawable.Drawable p0) {}
    public void setBackgroundResource(int p0) {}
    public void setClickable(boolean p0) {}
    public void setClipBounds(android.graphics.Rect p0) {}
    public void setCompoundDrawables(android.graphics.drawable.Drawable p0, android.graphics.drawable.Drawable p1, android.graphics.drawable.Drawable p2, android.graphics.drawable.Drawable p3) {}
    public void setCompoundDrawablesRelative(android.graphics.drawable.Drawable p0, android.graphics.drawable.Drawable p1, android.graphics.drawable.Drawable p2, android.graphics.drawable.Drawable p3) {}
    public void setCursorVisible(boolean p0) {}
    public void setCustomSelectionActionModeCallback(android.view.ActionMode.Callback p0) {}
    public void setEditableFactory(android.text.Editable.Factory p0) {}
    public void setEnabled(boolean p0) {}
    public void setError(java.lang.CharSequence p0) {}
    public void setFilters(android.text.InputFilter[] p0) {}
    public void setFocusable(boolean p0) {}
    public void setHint(int p0) {}
    public void setHintTextColor(int p0) {}
    public void setId(int p0) {}
    public void setImeHintLocales(android.os.LocaleList p0) {}
    public void setImeOptions(int p0) {}
    public void setInputType(int p0) {}
    public void setIsHandwritingDelegate(boolean p0) {}
    public void setKeyListener(android.text.method.KeyListener p0) {}
    public void setLines(int p0) {}
    public void setLongClickable(boolean p0) {}
    public void setMaxEms(int p0) {}
    public void setMaxLines(int p0) {}
    public void setMaxWidth(int p0) {}
    public void setMinEms(int p0) {}
    public void setMinWidth(int p0) {}
    public void setMinimumHeight(int p0) {}
    public void setOnClickListener(android.view.View.OnClickListener p0) {}
    public void setOnEditorActionListener(android.widget.TextView.OnEditorActionListener p0) {}
    public void setOnFocusChangeListener(android.view.View.OnFocusChangeListener p0) {}
    public void setOnKeyListener(android.view.View.OnKeyListener p0) {}
    public void setOnTouchListener(android.view.View.OnTouchListener p0) {}
    public void setPadding(int p0, int p1, int p2, int p3) {}
    public void setPaddingRelative(int p0, int p1, int p2, int p3) {}
    public void setPrivateImeOptions(java.lang.String p0) {}
    public void setRawInputType(int p0) {}
    public void setSaveEnabled(boolean p0) {}
    public void setSelection(int p0) {}
    public void setSelection(int p0, int p1) {}
    public void setSingleLine() {}
    public void setTag(java.lang.Object p0) {}
    public void setText(java.lang.CharSequence p0) {}
    public void setText(char[] p0, int p1, int p2) {}
    public void setText(java.lang.CharSequence p0, android.widget.TextView.BufferType p1) {}
    public void setTextAppearance(android.content.Context p0, int p1) {}
    public void setTextClassifier(android.view.textclassifier.TextClassifier p0) {}
    public void setTextSize(int p0, float p1) {}
    public void setTransformationMethod(android.text.method.TransformationMethod p0) {}
    public void setVisibility(int p0) {}
}
