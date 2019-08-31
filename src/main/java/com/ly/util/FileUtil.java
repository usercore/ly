package com.ly.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {


    /**
     * 读入TXT文件
     */
    public static void readFile() {
        String pathname = "input.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入TXT文件
     */
    public static void writeFile(String str,String fileName) {
        try {
            File writeName = new File(fileName); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(str); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String mikDir(String path){
    	
    	File file1=new File(path);
    	if(!file1.exists()){
    		file1.mkdir();
    	}
    	return path;
    }

    public static void main(String[] args) throws IOException {
    	String rootPath = "F:\\外包项目\\conf\\";
    	
    	File sourceFile = new File("F:\\外包项目\\雷泳\\capture\\target\\capture-1.0.0\\capture-1.0.0\\capture-1.0.0.jar");
    	
    	for(int i=1;i<=20;i++){
    		String filePath =  "";
    		String fileText = "excelFilePath=Z:\\\\\r\nexcelFilePostfix=-";
    		if(i<10){
    			String path = mikDir(rootPath + "capture0" + i);
    			filePath = mikDir(path+"\\conf");
    			
    			copyFile(sourceFile, new File(path + "\\capture-1.0.0.jar"));
    			
    		}else{
    			String path = mikDir(rootPath + "capture" + i);
    			filePath = mikDir(path+"\\conf");
    			
    			copyFile(sourceFile, new File(path + "\\capture-1.0.0.jar"));
    		}
    		fileText = fileText + i + ".xls";
    		writeFile(fileText, filePath + "\\config.properties");
    		
    	}
	}
    
    
    		/** 
    		 * 复制文件夹或文件夹 
    		 */  
    		/*public class CopyDirectory {  
    		    // 源文件夹   
    		    static String url1 = "f:/photos";  
    		    // 目标文件夹   
    		    static String url2 = "d:/tempPhotos";  
    		    public static void main(String args[]) throws IOException {  
    		        // 创建目标文件夹   
    		        (new File(url2)).mkdirs();  
    		        // 获取源文件夹当前下的文件或目录   
    		        File[] file = (new File(url1)).listFiles();  
    		        for (int i = 0; i < file.length; i++) {  
    		            if (file[i].isFile()) {  
    		                // 复制文件   
    		                copyFile(file[i],new File(url2+file[i].getName()));  
    		            }  
    		            if (file[i].isDirectory()) {  
    		                // 复制目录   
    		                String sourceDir=url1+File.separator+file[i].getName();  
    		                String targetDir=url2+File.separator+file[i].getName();  
    		                copyDirectiory(sourceDir, targetDir);  
    		            }  
    		        }  
    		    }  */
    		// 复制文件   
    		public static void copyFile(File sourceFile,File targetFile)   
    		throws IOException{  
    		        // 新建文件输入流并对它进行缓冲   
    		        FileInputStream input = new FileInputStream(sourceFile);  
    		        BufferedInputStream inBuff=new BufferedInputStream(input);  
    		  
    		        // 新建文件输出流并对它进行缓冲   
    		        FileOutputStream output = new FileOutputStream(targetFile);  
    		        BufferedOutputStream outBuff=new BufferedOutputStream(output);  
    		          
    		        // 缓冲数组   
    		        byte[] b = new byte[1024 * 5];  
    		        int len;  
    		        while ((len =inBuff.read(b)) != -1) {  
    		            outBuff.write(b, 0, len);  
    		        }  
    		        // 刷新此缓冲的输出流   
    		        outBuff.flush();  
    		          
    		        //关闭流   
    		        inBuff.close();  
    		        outBuff.close();  
    		        output.close();  
    		        input.close();  
    		    }  
    		    // 复制文件夹   
    		    public static void copyDirectiory(String sourceDir, String targetDir)  
    		            throws IOException {  
    		        // 新建目标目录   
    		        (new File(targetDir)).mkdirs();  
    		        // 获取源文件夹当前下的文件或目录   
    		        File[] file = (new File(sourceDir)).listFiles();  
    		        for (int i = 0; i < file.length; i++) {  
    		            if (file[i].isFile()) {  
    		                // 源文件   
    		                File sourceFile=file[i];  
    		                // 目标文件   
    		               File targetFile=new   
    		File(new File(targetDir).getAbsolutePath()  
    		+File.separator+file[i].getName());  
    		                copyFile(sourceFile,targetFile);  
    		            }  
    		            if (file[i].isDirectory()) {  
    		                // 准备复制的源文件夹   
    		                String dir1=sourceDir + "/" + file[i].getName();  
    		                // 准备复制的目标文件夹   
    		                String dir2=targetDir + "/"+ file[i].getName();  
    		                copyDirectiory(dir1, dir2);  
    		            }  
    		        }  
    		}
}
