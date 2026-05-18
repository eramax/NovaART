package android.animation;

public class ObjectAnimator extends ValueAnimator {

    private Object mTarget;
    private String mPropertyName;

    private ObjectAnimator() {}

    public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator oa = new ObjectAnimator();
        oa.mTarget = target;
        oa.mPropertyName = propertyName;
        oa.setFloatValues(values);
        return oa;
    }

    public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        ObjectAnimator oa = new ObjectAnimator();
        oa.mTarget = target;
        oa.mPropertyName = propertyName;
        oa.setIntValues(values);
        return oa;
    }

    public static ObjectAnimator ofObject(Object target, String propertyName,
            TypeEvaluator evaluator, Object... values) {
        ObjectAnimator oa = new ObjectAnimator();
        oa.mTarget = target;
        oa.mPropertyName = propertyName;
        return oa;
    }

    public static ObjectAnimator ofArgb(Object target, String propertyName, int... values) {
        ObjectAnimator oa = new ObjectAnimator();
        oa.mTarget = target;
        oa.mPropertyName = propertyName;
        oa.setIntValues(values);
        return oa;
    }

    public String getPropertyName() { return mPropertyName; }
    public void setPropertyName(String propertyName) { mPropertyName = propertyName; }

    @Override
    public void setTarget(Object target) { mTarget = target; }
    public Object getTarget() { return mTarget; }

    public ObjectAnimator clone() { return (ObjectAnimator) super.clone(); }
}
