import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

class Moyu{
    public static void main(String[] args) throws IOException {
        //创建文件，参数为绝对地址。（可以任意更改）
        File file=new File("C:\\Users\\Lenovo\\Desktop\\novel.txt");
        //判断文件是否存在，若不存在，则创建一个新的文件。
        if(!file.exists()){
            file.createNewFile();
        }
        //将从控制台输入的字节流信息转换成字符流
        InputStreamReader isr =new InputStreamReader(System.in);
        //用isr作为参数创建缓冲读取流
        BufferedReader br=new BufferedReader(isr);
        //一次读取一行，将读入的信息放到str中
        String str=br.readLine();
        //用文件的对象创建文件写入流
        FileWriter fw=new FileWriter(file);
        //用文件写入流的对象，创建缓冲写入流。（可以一次写入多个字节）
        BufferedWriter bw=new BufferedWriter(fw);
        while(!str.equals("#")){
            //将控制台的内容写入到文件中
            bw.write(str);
            //换行
            bw.newLine();
            //清空信息
            bw.flush();
            //接着读取一行信息
            str = br.readLine();
        }
        //关闭流
        br.close();
        isr.close();
        bw.close();
        fw.close();
    }
}