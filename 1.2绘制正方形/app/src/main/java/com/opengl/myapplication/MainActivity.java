package com.opengl.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity {

    GLSurfaceView sv;
    boolean isNewSV = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sv = new GLSurfaceView(this);
        sv.setEGLContextClientVersion(3);
        sv.setRenderer(new FRenderer());

        isNewSV = true;
        setContentView(sv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isNewSV)
        {
            sv.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNewSV)
        {
            sv.onResume();
        }
    }
}
class FRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "package com.opengl.myapplication;Class FRenderer";
    private final String VertexShaderCode =
                    "#version 300 es\n" +
                    "\n" +
                    "layout(location = 0) in vec3 a_Position;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_Position = vec4(a_Position,1.0f);\n" +
                    "}\n";
    private final String FragmentShaderCode =
                    "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "\n" +
                    "uniform vec4 a_Color;\n" +
                    "out vec4 FragColor;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    FragColor = a_Color;\n" +
                    "}";
    private FloatBuffer buffer;
    private int ShaderProgram;
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(1.0f,0.0f,0.0f,1.0f);
        float Vertex[] =
                {
                        // 第一个三角形
                        0.5f, 0.5f,    // 右上角
                        0.5f, -0.5f,   // 右下角
                        -0.5f, 0.5f,   // 左上角
                        // 第二个三角形
                        0.5f, -0.5f,   // 右下角
                        -0.5f, -0.5f,  // 左下角
                        -0.5f, 0.5f   // 左上角
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
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //清空缓冲区与深度缓冲区
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        //使用着色器程序
        GLES30.glUseProgram(ShaderProgram);
        //启用顶点属性,参数 0 是顶点着色器中定义的 a_Position 属性变量,即 (location = 0)
        GLES30.glEnableVertexAttribArray(0);
        //配置顶点属性,第一个参数是顶点属性ID,每个顶点属性的组件数量,顶点数据类型为float,固定点数据值不应该被归一化
        GLES30.glVertexAttribPointer(0,2,GLES30.GL_FLOAT,false,0,buffer);
        //找到 a_Color 并设置它的值
        GLES30.glUniform4f(GLES30.glGetUniformLocation(ShaderProgram,"a_Color"),0.3f,0.5f,0.7f,1.0f);
        //每三个点组成一个三角形,从 0 索引开始画,一共有 3 个顶点
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,6);
        //禁用顶点属性
        GLES30.glDisableVertexAttribArray(0);
    }
}