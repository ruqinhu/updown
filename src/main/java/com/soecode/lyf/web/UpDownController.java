package com.soecode.lyf.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
/*@RequestMapping("/book") // url:/模块/资源/{id}/细分 /seckill/list
*/public class UpDownController {



	
	@RequestMapping(value = "/updown")
	public String upDown() throws UnsupportedEncodingException {	
	
		
		return "/updown";
//		return "/admin/article/article_pager_list";
	}
	
	
	@RequestMapping(value = "/")
	public String upDown1() throws UnsupportedEncodingException {	
	
		
		return "/updown";
//		return "/admin/article/article_pager_list";
	}
	
	/**  
     * 文件上传功能  
     * @param file  
     * @return  
     * @throws IOException   
     */  
    @RequestMapping(value="/file/upload",method=RequestMethod.POST)  
    
    public String upload(MultipartFile file,HttpServletRequest request,HttpServletResponse response) throws IOException{  
        String path = request.getSession().getServletContext().getRealPath("upload");  
        System.out.print(path);
        String fileName = file.getOriginalFilename();    
        File dir = new File(path,fileName);          
        if(!dir.exists()){  
            dir.mkdirs();  
        }  
        //MultipartFile自带的解析方法  
        file.transferTo(dir); 
        return "redirect:/file/listFile";
    }  
      

    
    
    /** 
     * 列出所有的图片 
     */  
    @RequestMapping("/file/listFile")  
    public String listFile(HttpServletRequest request,  
            HttpServletResponse response) {  
        // 获取上传文件的目录  
        ServletContext sc = request.getSession().getServletContext();  
        // 上传位置  
        String uploadFilePath = sc.getRealPath("upload"); // 设定文件保存的目录  
        System.out.println(uploadFilePath);
        // 存储要下载的文件名  
        Map<String, Object> fileNameMap = new HashMap<String, Object>();  
        // 递归遍历filepath目录下的所有文件和目录，将文件的文件名存储到map集合中  
        File filelist=new File(uploadFilePath);
        File[] files=filelist.listFiles();
        if(files!=null) {
        	for(File file :files) {
        		fileNameMap.put(file.getName(), file);
        	}
        }
/*        listfile(new File(uploadFilePath), fileNameMap);// File既可以代表一个文件也可以代表一个目录  
*/        // 将Map集合发送到listfile.jsp页面进行显示  
        request.setAttribute("fileNameMap", fileNameMap);  
        return "listFile";  
    } 
    
    
    @RequestMapping("/downFile")  
    public void downFile(HttpServletRequest request,  
            HttpServletResponse response) {  
        System.out.println("1");  
        // 得到要下载的文件名  
        String fileName = request.getParameter("filename");  
        System.out.println("2");  
        try {  
            fileName = new String(fileName.getBytes("iso8859-1"), "UTF-8");  
            System.out.println("3");  
            // 获取上传文件的目录  
            ServletContext sc = request.getSession().getServletContext();  
            System.out.println("4");  
            // 上传位置  
            String fileSaveRootPath = sc.getRealPath("/upload");   
              
            System.out.println(fileSaveRootPath + "\\" + fileName);  
            // 得到要下载的文件  
            File file = new File(fileSaveRootPath + "\\" + fileName);  
              
            // 如果文件不存在  
            if (!file.exists()) {  
                request.setAttribute("message", "您要下载的资源已被删除！！");  
                System.out.println("您要下载的资源已被删除！！");  
                return;  
            }  
            // 处理文件名  
            String realname = fileName.substring(fileName.indexOf("_") + 1);  
            // 设置响应头，控制浏览器下载该文件  
            response.setHeader("content-disposition", "attachment;filename="  
                    + URLEncoder.encode(realname, "UTF-8"));  
            // 读取要下载的文件，保存到文件输入流  
            FileInputStream in = new FileInputStream(fileSaveRootPath + "\\" + fileName);  
            // 创建输出流  
            OutputStream out = response.getOutputStream();  
            // 创建缓冲区  
            byte buffer[] = new byte[1024];  
            int len = 0;  
            // 循环将输入流中的内容读取到缓冲区当中  
            while ((len = in.read(buffer)) > 0) {  
                // 输出缓冲区的内容到浏览器，实现文件下载  
                out.write(buffer, 0, len);  
            }  
          
            
            // 关闭文件输入流  
            in.close();  
            // 关闭输出流  
            out.close();  
        } catch (Exception e) {  
      
        }  
    }  
    
    /**  
     * 文件下载功能  
     * @param request  
     * @param response  
     * @throws Exception  
     */  
//    @RequestMapping("/file/down")  
//    public void down(HttpServletRequest request,HttpServletResponse response) throws Exception{  
//        //模拟文件，myfile.txt为需要下载的文件  
//        String fileName = request.getSession().getServletContext().getRealPath("upload")+"/myfile.txt";  
//        //获取输入流  
//        InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileName)));  
//        //假如以中文名下载的话  
//        String filename = "下载文件.txt";  
//        //转码，免得文件名中文乱码  
//        filename = URLEncoder.encode(filename,"UTF-8");  
//        //设置文件下载头  
//        response.addHeader("Content-Disposition", "attachment;filename=" + filename);    
//        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型    
//        response.setContentType("multipart/form-data");   
//        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());  
//        int len = 0;  
//        while((len = bis.read()) != -1){  
//            out.write(len);  
//            out.flush();  
//        }  
//        out.close();  
//    }  
}
