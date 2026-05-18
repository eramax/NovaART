package android.opengl;

public class GLES30 {
    public void glBeginQuery(int p0, int p1) {}
    public void glBeginTransformFeedback(int p0) {}
    public void glBindBufferBase(int p0, int p1, int p2) {}
    public void glBindBufferRange(int p0, int p1, int p2, int p3, int p4) {}
    public void glBindSampler(int p0, int p1) {}
    public void glBindTransformFeedback(int p0, int p1) {}
    public void glBindVertexArray(int p0) {}
    public void glBlitFramebuffer(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9) {}
    public void glClearBufferfi(int p0, int p1, float p2, int p3) {}
    public void glClearBufferfv(int p0, int p1, java.nio.FloatBuffer p2) {}
    public void glClearBufferiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glClearBufferuiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glCopyBufferSubData(int p0, int p1, int p2, int p3, int p4) {}
    public void glCopyTexSubImage3D(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8) {}
    public void glDeleteQueries(int p0, java.nio.IntBuffer p1) {}
    public void glDeleteQueries(int p0, int[] p1, int p2) {}
    public void glDeleteSamplers(int p0, java.nio.IntBuffer p1) {}
    public void glDeleteSamplers(int p0, int[] p1, int p2) {}
    public void glDeleteTransformFeedbacks(int p0, java.nio.IntBuffer p1) {}
    public void glDeleteTransformFeedbacks(int p0, int[] p1, int p2) {}
    public void glDeleteVertexArrays(int p0, java.nio.IntBuffer p1) {}
    public void glDeleteVertexArrays(int p0, int[] p1, int p2) {}
    public void glDrawArraysInstanced(int p0, int p1, int p2, int p3) {}
    public void glDrawBuffers(int p0, java.nio.IntBuffer p1) {}
    public void glDrawElementsInstanced(int p0, int p1, int p2, int p3, int p4) {}
    public void glDrawRangeElements(int p0, int p1, int p2, int p3, int p4, int p5) {}
    public void glEndQuery(int p0) {}
    public void glEndTransformFeedback() {}
    public void glFlushMappedBufferRange(int p0, int p1, int p2) {}
    public void glFramebufferTextureLayer(int p0, int p1, int p2, int p3, int p4) {}
    public void glGenQueries(int p0, java.nio.IntBuffer p1) {}
    public void glGenQueries(int p0, int[] p1, int p2) {}
    public void glGenSamplers(int p0, java.nio.IntBuffer p1) {}
    public void glGenSamplers(int p0, int[] p1, int p2) {}
    public void glGenTransformFeedbacks(int p0, java.nio.IntBuffer p1) {}
    public void glGenTransformFeedbacks(int p0, int[] p1, int p2) {}
    public void glGenVertexArrays(int p0, java.nio.IntBuffer p1) {}
    public void glGenVertexArrays(int p0, int[] p1, int p2) {}
    public void glGetActiveUniformBlockName(int p0, int p1, java.nio.Buffer p2, java.nio.Buffer p3) {}
    public java.lang.String glGetActiveUniformBlockName(int p0, int p1) { return null; }
    public void glGetActiveUniformBlockiv(int p0, int p1, int p2, java.nio.IntBuffer p3) {}
    public void glGetActiveUniformsiv(int p0, int p1, java.nio.IntBuffer p2, int p3, java.nio.IntBuffer p4) {}
    public void glGetBufferParameteri64v(int p0, int p1, java.nio.LongBuffer p2) {}
    public java.nio.Buffer glGetBufferPointerv(int p0, int p1) { return null; }
    public int glGetFragDataLocation(int p0, java.lang.String p1) { return 0; }
    public void glGetInteger64v(int p0, java.nio.LongBuffer p1) {}
    public void glGetQueryObjectuiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glGetQueryiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glGetSamplerParameterfv(int p0, int p1, java.nio.FloatBuffer p2) {}
    public void glGetSamplerParameteriv(int p0, int p1, java.nio.IntBuffer p2) {}
    public java.lang.String glGetStringi(int p0, int p1) { return null; }
    public int glGetUniformBlockIndex(int p0, java.lang.String p1) { return 0; }
    public void glGetUniformIndices(int p0, java.lang.String[] p1, java.nio.IntBuffer p2) {}
    public void glGetUniformuiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glGetVertexAttribIiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glGetVertexAttribIuiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glInvalidateFramebuffer(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glInvalidateSubFramebuffer(int p0, int p1, java.nio.IntBuffer p2, int p3, int p4, int p5, int p6) {}
    public boolean glIsQuery(int p0) { return false; }
    public boolean glIsSampler(int p0) { return false; }
    public boolean glIsTransformFeedback(int p0) { return false; }
    public boolean glIsVertexArray(int p0) { return false; }
    public java.nio.Buffer glMapBufferRange(int p0, int p1, int p2, int p3) { return null; }
    public void glPauseTransformFeedback() {}
    public void glProgramParameteri(int p0, int p1, int p2) {}
    public void glReadBuffer(int p0) {}
    public void glRenderbufferStorageMultisample(int p0, int p1, int p2, int p3, int p4) {}
    public void glResumeTransformFeedback() {}
    public void glSamplerParameterf(int p0, int p1, float p2) {}
    public void glSamplerParameterfv(int p0, int p1, java.nio.FloatBuffer p2) {}
    public void glSamplerParameteri(int p0, int p1, int p2) {}
    public void glSamplerParameteriv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glTexImage3D(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9) {}
    public void glTexSubImage3D(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9, int p10) {}
    public void glTransformFeedbackVaryings(int p0, java.lang.String[] p1, int p2) {}
    public void glUniform1uiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glUniform3uiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glUniform4uiv(int p0, int p1, java.nio.IntBuffer p2) {}
    public void glUniformBlockBinding(int p0, int p1, int p2) {}
    public void glUniformMatrix2x3fv(int p0, int p1, boolean p2, java.nio.FloatBuffer p3) {}
    public void glUniformMatrix2x4fv(int p0, int p1, boolean p2, java.nio.FloatBuffer p3) {}
    public void glUniformMatrix3x2fv(int p0, int p1, boolean p2, java.nio.FloatBuffer p3) {}
    public void glUniformMatrix3x4fv(int p0, int p1, boolean p2, java.nio.FloatBuffer p3) {}
    public void glUniformMatrix4x2fv(int p0, int p1, boolean p2, java.nio.FloatBuffer p3) {}
    public void glUniformMatrix4x3fv(int p0, int p1, boolean p2, java.nio.FloatBuffer p3) {}
    public boolean glUnmapBuffer(int p0) { return false; }
    public void glVertexAttribDivisor(int p0, int p1) {}
    public void glVertexAttribI4i(int p0, int p1, int p2, int p3, int p4) {}
    public void glVertexAttribI4ui(int p0, int p1, int p2, int p3, int p4) {}
    public void glVertexAttribIPointer(int p0, int p1, int p2, int p3, int p4) {}
}
