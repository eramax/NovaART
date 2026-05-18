package android.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteBuffer;

public class GLES20 {
    public static final int GL_ES_VERSION_2_0                               = 1;
    public static final int GL_DEPTH_BUFFER_BIT                             = 0x00000100;
    public static final int GL_STENCIL_BUFFER_BIT                           = 0x00000400;
    public static final int GL_COLOR_BUFFER_BIT                             = 0x00004000;
    public static final int GL_FALSE                                         = 0;
    public static final int GL_TRUE                                          = 1;
    public static final int GL_POINTS                                        = 0x0000;
    public static final int GL_LINES                                         = 0x0001;
    public static final int GL_LINE_LOOP                                     = 0x0002;
    public static final int GL_LINE_STRIP                                    = 0x0003;
    public static final int GL_TRIANGLES                                     = 0x0004;
    public static final int GL_TRIANGLE_STRIP                                = 0x0005;
    public static final int GL_TRIANGLE_FAN                                  = 0x0006;
    public static final int GL_ZERO                                          = 0;
    public static final int GL_ONE                                           = 1;
    public static final int GL_SRC_COLOR                                     = 0x0300;
    public static final int GL_ONE_MINUS_SRC_COLOR                          = 0x0301;
    public static final int GL_SRC_ALPHA                                     = 0x0302;
    public static final int GL_ONE_MINUS_SRC_ALPHA                          = 0x0303;
    public static final int GL_DST_ALPHA                                     = 0x0304;
    public static final int GL_ONE_MINUS_DST_ALPHA                          = 0x0305;
    public static final int GL_DST_COLOR                                     = 0x0306;
    public static final int GL_ONE_MINUS_DST_COLOR                          = 0x0307;
    public static final int GL_SRC_ALPHA_SATURATE                           = 0x0308;
    public static final int GL_FUNC_ADD                                      = 0x8006;
    public static final int GL_BLEND_EQUATION                               = 0x8009;
    public static final int GL_BLEND_EQUATION_RGB                           = 0x8009;
    public static final int GL_BLEND_EQUATION_ALPHA                         = 0x883D;
    public static final int GL_FUNC_SUBTRACT                                = 0x800A;
    public static final int GL_FUNC_REVERSE_SUBTRACT                        = 0x800B;
    public static final int GL_BLEND_DST_RGB                                = 0x80C8;
    public static final int GL_BLEND_SRC_RGB                                = 0x80C9;
    public static final int GL_BLEND_DST_ALPHA                              = 0x80CA;
    public static final int GL_BLEND_SRC_ALPHA                              = 0x80CB;
    public static final int GL_CONSTANT_COLOR                               = 0x8001;
    public static final int GL_ONE_MINUS_CONSTANT_COLOR                     = 0x8002;
    public static final int GL_CONSTANT_ALPHA                               = 0x8003;
    public static final int GL_ONE_MINUS_CONSTANT_ALPHA                     = 0x8004;
    public static final int GL_BLEND_COLOR                                  = 0x8005;
    public static final int GL_ARRAY_BUFFER                                 = 0x8892;
    public static final int GL_ELEMENT_ARRAY_BUFFER                        = 0x8893;
    public static final int GL_ARRAY_BUFFER_BINDING                        = 0x8894;
    public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING                = 0x8895;
    public static final int GL_STREAM_DRAW                                  = 0x88E0;
    public static final int GL_STATIC_DRAW                                  = 0x88E4;
    public static final int GL_DYNAMIC_DRAW                                 = 0x88E8;
    public static final int GL_FRAGMENT_SHADER                              = 0x8B30;
    public static final int GL_VERTEX_SHADER                                = 0x8B31;
    public static final int GL_COMPILE_STATUS                               = 0x8B81;
    public static final int GL_LINK_STATUS                                  = 0x8B82;
    public static final int GL_VALIDATE_STATUS                              = 0x8B83;
    public static final int GL_INFO_LOG_LENGTH                              = 0x8B84;
    public static final int GL_TEXTURE_2D                                   = 0x0DE1;
    public static final int GL_TEXTURE_CUBE_MAP                             = 0x8513;
    public static final int GL_TEXTURE_BINDING_2D                           = 0x8069;
    public static final int GL_TEXTURE_BINDING_CUBE_MAP                    = 0x8514;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X                 = 0x8515;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X                 = 0x8516;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y                 = 0x8517;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y                 = 0x8518;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z                 = 0x8519;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z                 = 0x851A;
    public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE                   = 0x851C;
    public static final int GL_TEXTURE_MIN_FILTER                          = 0x2801;
    public static final int GL_TEXTURE_MAG_FILTER                          = 0x2800;
    public static final int GL_TEXTURE_WRAP_S                              = 0x2802;
    public static final int GL_TEXTURE_WRAP_T                              = 0x2803;
    public static final int GL_NEAREST                                      = 0x2600;
    public static final int GL_LINEAR                                       = 0x2601;
    public static final int GL_NEAREST_MIPMAP_NEAREST                      = 0x2700;
    public static final int GL_LINEAR_MIPMAP_NEAREST                       = 0x2701;
    public static final int GL_NEAREST_MIPMAP_LINEAR                       = 0x2702;
    public static final int GL_LINEAR_MIPMAP_LINEAR                        = 0x2703;
    public static final int GL_TEXTURE0                                     = 0x84C0;
    public static final int GL_TEXTURE1                                     = 0x84C1;
    public static final int GL_TEXTURE2                                     = 0x84C2;
    public static final int GL_TEXTURE3                                     = 0x84C3;
    public static final int GL_TEXTURE4                                     = 0x84C4;
    public static final int GL_TEXTURE5                                     = 0x84C5;
    public static final int GL_TEXTURE6                                     = 0x84C6;
    public static final int GL_TEXTURE7                                     = 0x84C7;
    public static final int GL_ACTIVE_TEXTURE                              = 0x84E0;
    public static final int GL_REPEAT                                       = 0x2901;
    public static final int GL_CLAMP_TO_EDGE                               = 0x812F;
    public static final int GL_MIRRORED_REPEAT                             = 0x8370;
    public static final int GL_RGBA                                         = 0x1908;
    public static final int GL_RGB                                          = 0x1907;
    public static final int GL_ALPHA                                        = 0x1906;
    public static final int GL_LUMINANCE                                    = 0x1909;
    public static final int GL_LUMINANCE_ALPHA                             = 0x190A;
    public static final int GL_UNSIGNED_BYTE                               = 0x1401;
    public static final int GL_UNSIGNED_SHORT                              = 0x1403;
    public static final int GL_UNSIGNED_SHORT_5_6_5                        = 0x8363;
    public static final int GL_UNSIGNED_SHORT_4_4_4_4                      = 0x8033;
    public static final int GL_UNSIGNED_SHORT_5_5_5_1                      = 0x8034;
    public static final int GL_FLOAT                                        = 0x1406;
    public static final int GL_SHORT                                        = 0x1402;
    public static final int GL_INT                                          = 0x1404;
    public static final int GL_BYTE                                         = 0x1400;
    public static final int GL_DEPTH_COMPONENT                             = 0x1902;
    public static final int GL_NO_ERROR                                     = 0;
    public static final int GL_INVALID_ENUM                                = 0x0500;
    public static final int GL_INVALID_VALUE                               = 0x0501;
    public static final int GL_INVALID_OPERATION                           = 0x0502;
    public static final int GL_OUT_OF_MEMORY                               = 0x0505;
    public static final int GL_BLEND                                        = 0x0BE2;
    public static final int GL_DEPTH_TEST                                  = 0x0B71;
    public static final int GL_CULL_FACE                                   = 0x0B44;
    public static final int GL_SCISSOR_TEST                                = 0x0C11;
    public static final int GL_DITHER                                      = 0x0BD0;
    public static final int GL_STENCIL_TEST                                = 0x0B90;
    public static final int GL_POLYGON_OFFSET_FILL                        = 0x8037;
    public static final int GL_SAMPLE_ALPHA_TO_COVERAGE                   = 0x809E;
    public static final int GL_SAMPLE_COVERAGE                            = 0x80A0;
    public static final int GL_FRONT                                       = 0x0404;
    public static final int GL_BACK                                        = 0x0405;
    public static final int GL_FRONT_AND_BACK                             = 0x0408;
    public static final int GL_FRAMEBUFFER_COMPLETE                       = 0x8CD5;
    public static final int GL_FRAMEBUFFER                                = 0x8D40;
    public static final int GL_RENDERBUFFER                               = 0x8D41;
    public static final int GL_COLOR_ATTACHMENT0                          = 0x8CE0;
    public static final int GL_DEPTH_ATTACHMENT                           = 0x8D00;
    public static final int GL_STENCIL_ATTACHMENT                         = 0x8D20;
    public static final int GL_NONE                                        = 0;
    public static final int GL_DEPTH_COMPONENT16                          = 0x81A5;
    public static final int GL_DEPTH_COMPONENT24                          = 0x81A6;
    public static final int GL_DEPTH24_STENCIL8                           = 0x88F0;
    public static final int GL_RGBA4                                       = 0x8056;
    public static final int GL_RGB5_A1                                     = 0x8057;
    public static final int GL_RGB565                                      = 0x8D62;

    private GLES20() {}

    /* Clear */
    public static native void glClear(int mask);
    public static native void glClearColor(float red, float green, float blue, float alpha);
    public static native void glClearDepthf(float depth);
    public static native void glClearStencil(int s);

    /* Viewport / Scissor */
    public static native void glViewport(int x, int y, int width, int height);
    public static native void glScissor(int x, int y, int width, int height);

    /* Enable / Disable */
    public static native void glEnable(int cap);
    public static native void glDisable(int cap);
    public static native void glDepthMask(boolean flag);
    public static native void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);

    /* Blend */
    public static native void glBlendFunc(int sfactor, int dfactor);
    public static native void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);
    public static native void glBlendEquation(int mode);
    public static native void glBlendColor(float red, float green, float blue, float alpha);

    /* Depth */
    public static native void glDepthFunc(int func);
    public static native void glDepthRangef(float zNear, float zFar);

    /* Cull */
    public static native void glCullFace(int mode);
    public static native void glFrontFace(int mode);

    /* Error */
    public static native int glGetError();

    /* Shaders */
    public static native int  glCreateShader(int type);
    public static native void glShaderSource(int shader, String string);
    public static native void glCompileShader(int shader);
    public static native void glDeleteShader(int shader);
    public static native void glGetShaderiv(int shader, int pname, int[] params, int offset);
    public static native String glGetShaderInfoLog(int shader);
    public static native boolean glIsShader(int shader);

    /* Programs */
    public static native int  glCreateProgram();
    public static native void glAttachShader(int program, int shader);
    public static native void glDetachShader(int program, int shader);
    public static native void glLinkProgram(int program);
    public static native void glUseProgram(int program);
    public static native void glDeleteProgram(int program);
    public static native void glValidateProgram(int program);
    public static native void glGetProgramiv(int program, int pname, int[] params, int offset);
    public static native String glGetProgramInfoLog(int program);
    public static native boolean glIsProgram(int program);

    /* Attributes */
    public static native int  glGetAttribLocation(int program, String name);
    public static native void glBindAttribLocation(int program, int index, String name);
    public static native void glEnableVertexAttribArray(int index);
    public static native void glDisableVertexAttribArray(int index);
    public static native void glVertexAttribPointer(int indx, int size, int type,
            boolean normalized, int stride, Buffer ptr);
    public static native void glVertexAttribPointer(int indx, int size, int type,
            boolean normalized, int stride, int offset);
    public static native void glVertexAttrib1f(int indx, float x);
    public static native void glVertexAttrib2f(int indx, float x, float y);
    public static native void glVertexAttrib3f(int indx, float x, float y, float z);
    public static native void glVertexAttrib4f(int indx, float x, float y, float z, float w);
    public static native void glVertexAttrib1fv(int indx, float[] values, int offset);
    public static native void glVertexAttrib2fv(int indx, float[] values, int offset);
    public static native void glVertexAttrib3fv(int indx, float[] values, int offset);
    public static native void glVertexAttrib4fv(int indx, float[] values, int offset);

    /* Uniforms */
    public static native int  glGetUniformLocation(int program, String name);
    public static native void glUniform1i(int location, int x);
    public static native void glUniform2i(int location, int x, int y);
    public static native void glUniform3i(int location, int x, int y, int z);
    public static native void glUniform4i(int location, int x, int y, int z, int w);
    public static native void glUniform1f(int location, float x);
    public static native void glUniform2f(int location, float x, float y);
    public static native void glUniform3f(int location, float x, float y, float z);
    public static native void glUniform4f(int location, float x, float y, float z, float w);
    public static native void glUniform1fv(int location, int count, float[] v, int offset);
    public static native void glUniform2fv(int location, int count, float[] v, int offset);
    public static native void glUniform3fv(int location, int count, float[] v, int offset);
    public static native void glUniform4fv(int location, int count, float[] v, int offset);
    public static native void glUniform1iv(int location, int count, int[] v, int offset);
    public static native void glUniformMatrix2fv(int location, int count, boolean transpose,
            float[] value, int offset);
    public static native void glUniformMatrix3fv(int location, int count, boolean transpose,
            float[] value, int offset);
    public static native void glUniformMatrix4fv(int location, int count, boolean transpose,
            float[] value, int offset);

    /* Textures */
    public static native void glGenTextures(int n, int[] textures, int offset);
    public static native void glDeleteTextures(int n, int[] textures, int offset);
    public static native void glBindTexture(int target, int texture);
    public static native void glActiveTexture(int texture);
    public static native void glTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int format, int type, Buffer pixels);
    public static native void glTexSubImage2D(int target, int level, int xoffset, int yoffset,
            int width, int height, int format, int type, Buffer pixels);
    public static native void glTexParameteri(int target, int pname, int param);
    public static native void glTexParameterf(int target, int pname, float param);
    public static native void glTexParameterfv(int target, int pname, float[] params, int offset);
    public static native void glTexParameteriv(int target, int pname, int[] params, int offset);
    public static native void glGenerateMipmap(int target);
    public static native boolean glIsTexture(int texture);
    public static native int glGetTexParameteriv_single(int target, int pname);

    /* Draw */
    public static native void glDrawArrays(int mode, int first, int count);
    public static native void glDrawElements(int mode, int count, int type, Buffer indices);
    public static native void glDrawElements(int mode, int count, int type, int offset);
    public static native void glLineWidth(float width);
    public static native void glPointSize(float size);

    /* Buffers */
    public static native void glGenBuffers(int n, int[] buffers, int offset);
    public static native void glDeleteBuffers(int n, int[] buffers, int offset);
    public static native void glBindBuffer(int target, int buffer);
    public static native void glBufferData(int target, int size, Buffer data, int usage);
    public static native void glBufferSubData(int target, int offset, int size, Buffer data);
    public static native boolean glIsBuffer(int buffer);

    /* Framebuffers */
    public static native void glGenFramebuffers(int n, int[] framebuffers, int offset);
    public static native void glDeleteFramebuffers(int n, int[] framebuffers, int offset);
    public static native void glBindFramebuffer(int target, int framebuffer);
    public static native void glFramebufferTexture2D(int target, int attachment, int textarget,
            int texture, int level);
    public static native void glFramebufferRenderbuffer(int target, int attachment,
            int renderbuffertarget, int renderbuffer);
    public static native int  glCheckFramebufferStatus(int target);

    /* Renderbuffers */
    public static native void glGenRenderbuffers(int n, int[] renderbuffers, int offset);
    public static native void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset);
    public static native void glBindRenderbuffer(int target, int renderbuffer);
    public static native void glRenderbufferStorage(int target, int internalformat,
            int width, int height);

    /* Misc */
    public static native void glFlush();
    public static native void glFinish();
    public static native String glGetString(int name);
    public static native void glGetIntegerv(int pname, int[] params, int offset);
    public static native void glGetFloatv(int pname, float[] params, int offset);
    public static native void glGetBooleanv(int pname, boolean[] params, int offset);
    public static native void glPolygonOffset(float factor, float units);
    public static native void glSampleCoverage(float value, boolean invert);
    public static native void glStencilFunc(int func, int ref, int mask);
    public static native void glStencilMask(int mask);
    public static native void glStencilOp(int fail, int zfail, int zpass);
    public static native void glReadPixels(int x, int y, int width, int height,
            int format, int type, Buffer pixels);
    public static native void glPixelStorei(int pname, int param);
    public static native void glCopyTexImage2D(int target, int level, int internalformat,
            int x, int y, int width, int height, int border);
    public static native void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset,
            int x, int y, int width, int height);
    public static native int glGetUniformBlockIndex(int program, String uniformBlockName);
    public static native void glUniformBlockBinding(int program, int uniformBlockIndex,
            int uniformBlockBinding);

    /* Buffer and array objects */
    public static native void glGenVertexArraysOES(int n, int[] arrays, int offset);
    public static native void glBindVertexArrayOES(int array);
    public static native void glDeleteVertexArraysOES(int n, int[] arrays, int offset);
}
