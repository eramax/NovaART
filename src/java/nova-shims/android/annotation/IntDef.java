package android.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.ANNOTATION_TYPE})
public @interface IntDef {
    int[] value() default {};
    boolean flag() default false;
    boolean open() default false;
    String[] prefix() default {};
    String[] suffix() default {};
}
