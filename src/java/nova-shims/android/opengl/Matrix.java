package android.opengl;

public class Matrix {
    private Matrix() {}

    public static void setIdentityM(float[] sm, int smOffset) {
        for (int i = 0; i < 16; i++) sm[smOffset + i] = 0;
        for (int i = 0; i < 4; i++) sm[smOffset + i * 5] = 1.0f;
    }

    public static void scaleM(float[] sm, int smOffset, float[] m, int mOffset,
            float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            int mi = mOffset + i;
            int si = smOffset + i;
            sm[si    ] = m[mi    ] * x;
            sm[si + 4] = m[mi + 4] * y;
            sm[si + 8] = m[mi + 8] * z;
            sm[si + 12] = m[mi + 12];
        }
    }

    public static void scaleM(float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            m[mOffset + i    ] *= x;
            m[mOffset + i + 4] *= y;
            m[mOffset + i + 8] *= z;
        }
    }

    public static void translateM(float[] tm, int tmOffset, float[] m, int mOffset,
            float x, float y, float z) {
        for (int i = 0; i < 12; i++) tm[tmOffset + i] = m[mOffset + i];
        for (int i = 0; i < 4; i++) {
            tm[tmOffset + 12 + i] = m[mOffset + i] * x + m[mOffset + 4 + i] * y
                    + m[mOffset + 8 + i] * z + m[mOffset + 12 + i];
        }
    }

    public static void translateM(float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            m[mOffset + 12 + i] += m[mOffset + i] * x + m[mOffset + 4 + i] * y
                    + m[mOffset + 8 + i] * z;
        }
    }

    public static void rotateM(float[] rm, int rmOffset, float[] m, int mOffset,
            float a, float x, float y, float z) {
        float[] r = new float[16];
        setRotateM(r, 0, a, x, y, z);
        multiplyMM(rm, rmOffset, m, mOffset, r, 0);
    }

    public static void rotateM(float[] m, int mOffset, float a, float x, float y, float z) {
        float[] temp = new float[32];
        setRotateM(temp, 0, a, x, y, z);
        multiplyMM(temp, 16, m, mOffset, temp, 0);
        System.arraycopy(temp, 16, m, mOffset, 16);
    }

    public static void setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z) {
        rm[rmOffset + 3] = 0;
        rm[rmOffset + 7] = 0;
        rm[rmOffset + 11] = 0;
        rm[rmOffset + 12] = 0;
        rm[rmOffset + 13] = 0;
        rm[rmOffset + 14] = 0;
        rm[rmOffset + 15] = 1;
        float s = (float) Math.sin(Math.toRadians(a));
        float c = (float) Math.cos(Math.toRadians(a));
        if (x == 1 && y == 0 && z == 0) {
            rm[rmOffset] = 1; rm[rmOffset+5] = c; rm[rmOffset+10] = c;
            rm[rmOffset+6] = s; rm[rmOffset+9] = -s;
            rm[rmOffset+1] = 0; rm[rmOffset+2] = 0; rm[rmOffset+4] = 0;
            rm[rmOffset+8] = 0;
        } else if (x == 0 && y == 1 && z == 0) {
            rm[rmOffset+5] = 1; rm[rmOffset] = c; rm[rmOffset+10] = c;
            rm[rmOffset+8] = s; rm[rmOffset+2] = -s;
            rm[rmOffset+1] = 0; rm[rmOffset+4] = 0; rm[rmOffset+6] = 0;
            rm[rmOffset+9] = 0;
        } else if (x == 0 && y == 0 && z == 1) {
            rm[rmOffset+10] = 1; rm[rmOffset] = c; rm[rmOffset+5] = c;
            rm[rmOffset+1] = s; rm[rmOffset+4] = -s;
            rm[rmOffset+2] = 0; rm[rmOffset+6] = 0; rm[rmOffset+8] = 0;
            rm[rmOffset+9] = 0;
        } else {
            float len = (float) Math.sqrt(x*x + y*y + z*z);
            if (len != 1) { x /= len; y /= len; z /= len; }
            float nc = 1.0f - c;
            float xy = x * y; float yz = y * z; float zx = z * x;
            float xs = x * s; float ys = y * s; float zs = z * s;
            rm[rmOffset]     = x*x*nc +  c; rm[rmOffset+4] = xy *nc - zs; rm[rmOffset+8]  = zx *nc + ys;
            rm[rmOffset+1]   = xy *nc + zs; rm[rmOffset+5] = y*y*nc +  c; rm[rmOffset+9]  = yz *nc - xs;
            rm[rmOffset+2]   = zx *nc - ys; rm[rmOffset+6] = yz *nc + xs; rm[rmOffset+10] = z*z*nc +  c;
        }
    }

    public static void multiplyMM(float[] result, int resultOffset,
            float[] lhs, int lhsOffset, float[] rhs, int rhsOffset) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += lhs[lhsOffset + k * 4 + j] * rhs[rhsOffset + i * 4 + k];
                }
                result[resultOffset + i * 4 + j] = sum;
            }
        }
    }

    public static void multiplyMV(float[] resultVec, int resultVecOffset,
            float[] lhsMat, int lhsMatOffset, float[] rhsVec, int rhsVecOffset) {
        for (int i = 0; i < 4; i++) {
            float sum = 0;
            for (int j = 0; j < 4; j++) {
                sum += lhsMat[lhsMatOffset + j * 4 + i] * rhsVec[rhsVecOffset + j];
            }
            resultVec[resultVecOffset + i] = sum;
        }
    }

    public static void frustumM(float[] m, int offset,
            float left, float right, float bottom, float top, float near, float far) {
        float r_width  = 1.0f / (right - left);
        float r_height = 1.0f / (top - bottom);
        float r_depth  = 1.0f / (near - far);
        float x = 2.0f * (near) * r_width;
        float y = 2.0f * (near) * r_height;
        float A = (right + left) * r_width;
        float B = (top + bottom) * r_height;
        float C = (far + near) * r_depth;
        float D = 2.0f * (far * near) * r_depth;
        m[offset]     = x; m[offset+5]  = y; m[offset+8] = A;
        m[offset+9]   = B; m[offset+10] = C; m[offset+14] = D;
        m[offset+11]  = -1.0f;
        m[offset+1]   = 0; m[offset+2] = 0; m[offset+3] = 0;
        m[offset+4]   = 0; m[offset+6] = 0; m[offset+7] = 0;
        m[offset+12]  = 0; m[offset+13] = 0; m[offset+15] = 0;
    }

    public static void orthoM(float[] m, int mOffset,
            float left, float right, float bottom, float top, float near, float far) {
        float r_width  = 1.0f / (right - left);
        float r_height = 1.0f / (top - bottom);
        float r_depth  = 1.0f / (far - near);
        float x  =  2.0f * r_width;
        float y  =  2.0f * r_height;
        float z  = -2.0f * r_depth;
        float tx = -(right + left) * r_width;
        float ty = -(top + bottom) * r_height;
        float tz = -(far + near) * r_depth;
        m[mOffset]     = x;  m[mOffset+5]  = y;  m[mOffset+10] = z;
        m[mOffset+12]  = tx; m[mOffset+13] = ty; m[mOffset+14] = tz;
        m[mOffset+15]  = 1;
        m[mOffset+1]   = 0; m[mOffset+2]  = 0; m[mOffset+3]  = 0;
        m[mOffset+4]   = 0; m[mOffset+6]  = 0; m[mOffset+7]  = 0;
        m[mOffset+8]   = 0; m[mOffset+9]  = 0; m[mOffset+11] = 0;
    }

    public static void setLookAtM(float[] rm, int rmOffset,
            float eyeX, float eyeY, float eyeZ,
            float centerX, float centerY, float centerZ,
            float upX, float upY, float upZ) {
        float fx = centerX - eyeX, fy = centerY - eyeY, fz = centerZ - eyeZ;
        float rlf = 1.0f / (float) Math.sqrt(fx*fx + fy*fy + fz*fz);
        fx *= rlf; fy *= rlf; fz *= rlf;
        float sx = fy*upZ - fz*upY, sy = fz*upX - fx*upZ, sz = fx*upY - fy*upX;
        float rls = 1.0f / (float) Math.sqrt(sx*sx + sy*sy + sz*sz);
        sx *= rls; sy *= rls; sz *= rls;
        float ux = sy*fz - sz*fy, uy = sz*fx - sx*fz, uz = sx*fy - sy*fx;
        rm[rmOffset]     = sx; rm[rmOffset+1]  = ux; rm[rmOffset+2]  = -fx; rm[rmOffset+3]  = 0;
        rm[rmOffset+4]   = sy; rm[rmOffset+5]  = uy; rm[rmOffset+6]  = -fy; rm[rmOffset+7]  = 0;
        rm[rmOffset+8]   = sz; rm[rmOffset+9]  = uz; rm[rmOffset+10] = -fz; rm[rmOffset+11] = 0;
        rm[rmOffset+12]  = 0;  rm[rmOffset+13] = 0;  rm[rmOffset+14] = 0;  rm[rmOffset+15] = 1;
        translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
    }

    public static boolean invertM(float[] mInv, int mInvOffset, float[] m, int mOffset) {
        float[] src = new float[16];
        System.arraycopy(m, mOffset, src, 0, 16);
        float[] dst = mInv;
        int di = mInvOffset;
        float det = src[0]*(src[5]*src[10]*src[15] - src[5]*src[11]*src[14]
                - src[9]*src[6]*src[15] + src[9]*src[7]*src[14]
                + src[13]*src[6]*src[11] - src[13]*src[7]*src[10])
                - src[4]*(src[1]*src[10]*src[15] - src[1]*src[11]*src[14]
                - src[9]*src[2]*src[15] + src[9]*src[3]*src[14]
                + src[13]*src[2]*src[11] - src[13]*src[3]*src[10])
                + src[8]*(src[1]*src[6]*src[15] - src[1]*src[7]*src[14]
                - src[5]*src[2]*src[15] + src[5]*src[3]*src[14]
                + src[13]*src[2]*src[7] - src[13]*src[3]*src[6])
                - src[12]*(src[1]*src[6]*src[11] - src[1]*src[7]*src[10]
                - src[5]*src[2]*src[11] + src[5]*src[3]*src[10]
                + src[9]*src[2]*src[7] - src[9]*src[3]*src[6]);
        if (det == 0) return false;
        float invDet = 1.0f / det;
        dst[di]    = invDet*(src[5]*(src[10]*src[15]-src[11]*src[14])-src[9]*(src[6]*src[15]-src[7]*src[14])+src[13]*(src[6]*src[11]-src[7]*src[10]));
        dst[di+4]  = -invDet*(src[4]*(src[10]*src[15]-src[11]*src[14])-src[8]*(src[6]*src[15]-src[7]*src[14])+src[12]*(src[6]*src[11]-src[7]*src[10]));
        dst[di+8]  = invDet*(src[4]*(src[9]*src[15]-src[11]*src[13])-src[8]*(src[5]*src[15]-src[7]*src[13])+src[12]*(src[5]*src[11]-src[7]*src[9]));
        dst[di+12] = -invDet*(src[4]*(src[9]*src[14]-src[10]*src[13])-src[8]*(src[5]*src[14]-src[6]*src[13])+src[12]*(src[5]*src[10]-src[6]*src[9]));
        dst[di+1]  = -invDet*(src[1]*(src[10]*src[15]-src[11]*src[14])-src[9]*(src[2]*src[15]-src[3]*src[14])+src[13]*(src[2]*src[11]-src[3]*src[10]));
        dst[di+5]  = invDet*(src[0]*(src[10]*src[15]-src[11]*src[14])-src[8]*(src[2]*src[15]-src[3]*src[14])+src[12]*(src[2]*src[11]-src[3]*src[10]));
        dst[di+9]  = -invDet*(src[0]*(src[9]*src[15]-src[11]*src[13])-src[8]*(src[1]*src[15]-src[3]*src[13])+src[12]*(src[1]*src[11]-src[3]*src[9]));
        dst[di+13] = invDet*(src[0]*(src[9]*src[14]-src[10]*src[13])-src[8]*(src[1]*src[14]-src[2]*src[13])+src[12]*(src[1]*src[10]-src[2]*src[9]));
        dst[di+2]  = invDet*(src[1]*(src[6]*src[15]-src[7]*src[14])-src[5]*(src[2]*src[15]-src[3]*src[14])+src[13]*(src[2]*src[7]-src[3]*src[6]));
        dst[di+6]  = -invDet*(src[0]*(src[6]*src[15]-src[7]*src[14])-src[4]*(src[2]*src[15]-src[3]*src[14])+src[12]*(src[2]*src[7]-src[3]*src[6]));
        dst[di+10] = invDet*(src[0]*(src[5]*src[15]-src[7]*src[13])-src[4]*(src[1]*src[15]-src[3]*src[13])+src[12]*(src[1]*src[7]-src[3]*src[5]));
        dst[di+14] = -invDet*(src[0]*(src[5]*src[14]-src[6]*src[13])-src[4]*(src[1]*src[14]-src[2]*src[13])+src[12]*(src[1]*src[6]-src[2]*src[5]));
        dst[di+3]  = -invDet*(src[1]*(src[6]*src[11]-src[7]*src[10])-src[5]*(src[2]*src[11]-src[3]*src[10])+src[9]*(src[2]*src[7]-src[3]*src[6]));
        dst[di+7]  = invDet*(src[0]*(src[6]*src[11]-src[7]*src[10])-src[4]*(src[2]*src[11]-src[3]*src[10])+src[8]*(src[2]*src[7]-src[3]*src[6]));
        dst[di+11] = -invDet*(src[0]*(src[5]*src[11]-src[7]*src[9])-src[4]*(src[1]*src[11]-src[3]*src[9])+src[8]*(src[1]*src[7]-src[3]*src[5]));
        dst[di+15] = invDet*(src[0]*(src[5]*src[10]-src[6]*src[9])-src[4]*(src[1]*src[10]-src[2]*src[9])+src[8]*(src[1]*src[6]-src[2]*src[5]));
        return true;
    }

    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }
}
