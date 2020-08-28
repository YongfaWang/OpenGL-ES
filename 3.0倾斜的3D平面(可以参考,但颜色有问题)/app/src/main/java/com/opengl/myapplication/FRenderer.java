package com.opengl.myapplication;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class FRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "package com.opengl.myapplication;Class FRenderer";
    private final int VERTEX_COUNT = 2;
    private final int COLOR_COUNT = 3;
    private final int STRIDE = (VERTEX_COUNT + COLOR_COUNT)*4;
    private final String VertexShaderCode =
                    "#version 300 es\n" +
                    "\n" +
                    "layout(location = 0) in vec4 a_Position;\n" +
                    "in vec3 a_Color;\n" +
                    "uniform mat4 matrix;\n" +
                    "out vec4 Color;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_Position = matrix * a_Position;\n" +
                    "    Color = vec4(a_Color,1.0);\n" +
                    "}\n";
    private final String FragmentShaderCode =
                    "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "\n" +
                    "out vec4 FragColor;\n" +
                    "in vec4 Color;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    FragColor = Color;\n" +
                    "}";
    private FloatBuffer buffer;
    private int ShaderProgram;
    private float[] matrix = new float[4 * 4];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(1.0f,0.0f,0.0f,1.0f);
        float Vertex[] =
                {
                        // X,Y,Z,W,R,G,B
                        -0.5f,-0.8f,0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                        0.5f, -0.8f,1.0f, 1.0f, 0.7f, 0.7f, 0.7f,
                        0.5f, 0.8f, 0.0f, 2.0f, 0.7f, 0.7f, 0.7f,
                        0.5f, 0.8f, 0.0f, 2.0f, 0.7f, 0.7f, 0.7f,
                        -0.5f,0.8f, 0.0f, 2.0f, 0.7f, 0.7f, 0.7f,
                        -0.5f,-0.8f,0.0f, 1.0f, 0.7f, 0.7f, 0.7f,
                        // 线
                        -0.5f,0.0f, 0.0f, 1.5f, 1.0f, 0.0f, 0.0f,
                        0.5f, 0.0f, 0.0f, 1.5f, 1.0f, 0.0f, 0.0f,
                        // 点
                        0.0f, -0.4f,0.0f,1.25f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.4f, 0.0f,1.75f, 1.0f, 0.0f, 0.0f
                };
        buffer = ByteBuffer.allocateDirect(Vertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        if(buffer == null)
        {
            System.out.println("FRenderer.onSurfaceCreated :: FloatBuffer对象为空.请在这里" + TAG + "下断点");
            return;
        }
        buffer.put(Vertex);
        buffer.position(0);
        //创建一个顶点着色器
        int VertexShader = loadShader(GLES30.GL_VERTEX_SHADER,VertexShaderCode);
        //创建一个片段着色器
        int FragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER,FragmentShaderCode);
        //如果有返回0就创建失败
        if(VertexShader == 0 || FragmentShader == 0)
        {
            System.out.println("FRenderer.onSurfaceCreated :: VertexShader || FragmentShader == 0.请在这里" + TAG + "下断点");
            return;
        }
        //创建着色器程序
        ShaderProgram = GLES30.glCreateProgram();
        //如果返回0就创建失败
        if(ShaderProgram == 0)
        {
            System.out.println("FRenderer.onSurfaceCreated :: ShaderProgram == 0.请在这里" + TAG + "下断点");
            return;
        }
        //将顶点着色器绑定到着色器程序
        GLES30.glAttachShader(ShaderProgram,VertexShader);
        //将片段着色器绑定到着色器程序
        GLES30.glAttachShader(ShaderProgram,FragmentShader);
        //链接着色器程序
        GLES30.glLinkProgram(ShaderProgram);
        int log[] = new int[1];
        GLES30.glGetShaderiv(ShaderProgram,GLES30.GL_LINK_STATUS,log,0);
        if(log[0] == 0)
        {
            System.out.println("FRenderer.onSurfaceCreated :: ShaderProgram_Link.请在这里" + TAG + "下断点");
            System.out.println(GLES30.glGetProgramInfoLog(ShaderProgram));
            return;
        }
        GLES30.glDeleteShader(VertexShader);
        GLES30.glDeleteShader(FragmentShader);
    }

    private int loadShader(int type,String shaderCode) {
        //创建一个 type 类型着色器,可能传进来的 type 是 顶点着色器或片段着色器 的常量,这代表在创建时要创建的着色器类型
        int Shader = GLES30.glCreateShader(type);
        //将着色器源码绑定到着色器
        GLES30.glShaderSource(Shader,shaderCode);
        //编译着色器
        GLES30.glCompileShader(Shader);
        int log[] = new int[1];
        //返回编译错误
        GLES30.glGetShaderiv(Shader,GLES30.GL_COMPILE_STATUS,log,0);
        if(log[0] == 0)
        {
            System.out.println("FRenderer.onSurfaceCreated :: ShaderProgram_Compile.请在这里" + TAG + "下断点");
            String s = GLES30.glGetShaderInfoLog(Shader);
            System.err.println(s);
            //如果失败返回 0 值,可以在接收返回值的变量判断是否为0,如果为0则编译失败
            return 0;
        }
        return Shader;
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES30.glViewport(0,0,i,i1);
        final float aspectRatio = i > i1 ? (float) i / (float) i1 : (float) i1 / (float) i;
        if(i > i1)
        {
            //横屏
            Matrix.orthoM(matrix,0,-aspectRatio ,aspectRatio ,-1f,1f,-1f,1f);
        }
        else
        {
            //竖屏
            Matrix.orthoM(matrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //清空缓冲区与深度缓冲区
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        //使用着色器程序
        GLES30.glUseProgram(ShaderProgram);
        buffer.position(0);
        //正方形顶点
        GLES30.glEnableVertexAttribArray(GLES30.glGetAttribLocation(ShaderProgram,"a_Color"));
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0,4, GLES20.GL_FLOAT,false,(4+3)*4,buffer);
        //正方形颜色
        buffer.position(5);
        GLES30.glVertexAttribPointer(GLES30.glGetAttribLocation(ShaderProgram,"a_Color"),3,GLES20.GL_FLOAT,false,(4+3)*4,buffer);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(ShaderProgram,"matrix"),1,false,matrix,0);
        GLES30.glLineWidth(15);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,6);
        GLES30.glDrawArrays(GLES30.GL_LINES,6,2);
        GLES30.glDrawArrays(GLES30.GL_POINTS,8,2);
        GLES30.glDisableVertexAttribArray(0);
    }
}