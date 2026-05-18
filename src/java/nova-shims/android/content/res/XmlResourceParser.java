package android.content.res;

public interface XmlResourceParser extends org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, java.lang.AutoCloseable {
    @Override
    String getAttributeNamespace(int index);

    @Override
    void close();
}
