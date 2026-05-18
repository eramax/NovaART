package android.content.res;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

final class EmptyXmlResourceParser implements XmlResourceParser {
    private int mEventType = START_DOCUMENT;

    @Override
    public void setFeature(String name, boolean state) {
    }

    @Override
    public boolean getFeature(String name) {
        return false;
    }

    @Override
    public void setProperty(String name, Object value) {
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public void setInput(java.io.Reader in) {
    }

    @Override
    public void setInput(java.io.InputStream inputStream, String inputEncoding) {
    }

    @Override
    public String getInputEncoding() {
        return null;
    }

    @Override
    public void defineEntityReplacementText(String entityName, String replacementText) {
    }

    @Override
    public int getNamespaceCount(int depth) {
        return 0;
    }

    @Override
    public String getNamespacePrefix(int pos) {
        return null;
    }

    @Override
    public String getNamespaceUri(int pos) {
        return null;
    }

    @Override
    public String getNamespace(String prefix) {
        return null;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public String getPositionDescription() {
        return "empty";
    }

    @Override
    public int getLineNumber() {
        return 0;
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public boolean isWhitespace() {
        return false;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        if (holderForStartAndLength != null && holderForStartAndLength.length >= 2) {
            holderForStartAndLength[0] = 0;
            holderForStartAndLength[1] = 0;
        }
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public boolean isEmptyElementTag() {
        return true;
    }

    @Override
    public int getAttributeCount() {
        return 0;
    }

    @Override
    public String getAttributeNamespace(int index) {
        return null;
    }

    @Override
    public String getAttributeName(int index) {
        return null;
    }

    @Override
    public String getAttributePrefix(int index) {
        return null;
    }

    @Override
    public String getAttributeType(int index) {
        return null;
    }

    @Override
    public boolean isAttributeDefault(int index) {
        return false;
    }

    @Override
    public String getAttributeValue(int index) {
        return null;
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        return null;
    }

    @Override
    public int getEventType() {
        return mEventType;
    }

    @Override
    public int next() {
        mEventType = END_DOCUMENT;
        return mEventType;
    }

    @Override
    public int nextToken() {
        return next();
    }

    @Override
    public void require(int type, String namespace, String name) {
    }

    @Override
    public String nextText() {
        return "";
    }

    @Override
    public int nextTag() {
        return END_DOCUMENT;
    }

    public int getAttributeNameResource(int index) {
        return 0;
    }

    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return defaultValue;
    }

    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        return defaultValue;
    }

    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        return defaultValue;
    }

    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        return defaultValue;
    }

    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        return defaultValue;
    }

    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        return defaultValue;
    }

    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return defaultValue;
    }

    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return defaultValue;
    }

    public int getAttributeResourceValue(int index, int defaultValue) {
        return defaultValue;
    }

    public int getAttributeIntValue(int index, int defaultValue) {
        return defaultValue;
    }

    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return defaultValue;
    }

    public float getAttributeFloatValue(int index, float defaultValue) {
        return defaultValue;
    }

    public String getIdAttribute() {
        return null;
    }

    public String getClassAttribute() {
        return null;
    }

    public int getIdAttributeResourceValue(int defaultValue) {
        return defaultValue;
    }

    public int getStyleAttribute() {
        return 0;
    }

    @Override
    public void close() {
    }
}
