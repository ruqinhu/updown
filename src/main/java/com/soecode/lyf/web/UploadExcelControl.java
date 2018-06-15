package com.soecode.lyf.web;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
	 
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.soecode.lyf.dao.BookDao;
import com.soecode.lyf.entity.Book;
import com.soecode.lyf.service.BookService;
import com.soecode.lyf.service.impl.UploadExcelService;
	 
	@Controller
	public class UploadExcelControl {
	 
	    @Autowired
	    private UploadExcelService uploadService;

	    @Autowired
	    private BookDao bookDao;
	    @RequestMapping("/export")
	    public @ResponseBody String export(HttpServletResponse response){    
	        response.setContentType("application/binary;charset=UTF-8");
	        try{
	            ServletOutputStream out=response.getOutputStream();
	            String fileName=new String(("UserInfo "+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())).getBytes(),"UTF-8");
	            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
	            String[] titles = { "图书Id", "图书名", "图书数量"}; 
	            uploadService.export(titles, out);
	            return "success";
	        } catch(Exception e){
	            e.printStackTrace();
	            return "导出信息失败";
	        }
	    }
	    
	    /**
	     * 描述：通过传统方式form表单提交方式导入excel文件
	     * @param request
	     * @throws Exception
	     */
	    @RequestMapping(value="/upload",method={RequestMethod.GET,RequestMethod.POST})
	    public  String  uploadExcel(HttpServletRequest request) throws Exception {
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
	        System.out.println("通过传统方式form表单提交方式导入excel文件！");

	        InputStream in =null;
	        List<List<Object>> listob = null;
	        MultipartFile file = multipartRequest.getFile("upfile");
	        if(file.isEmpty()){
	            throw new Exception("文件不存在！");
	        }
	        in = file.getInputStream();
	        listob = uploadService.getBankListByExcel(in,file.getOriginalFilename());
	        in.close();
            System.out.println(listob.size());
	        //该处可调用service相应方法进行数据保存到数据库中，现只对数据输出
	        for (int i = 0; i < listob.size(); i++) {
	            List<Object> lo = listob.get(i);
	            Book vo = new Book();
	            vo.setBookId(Long.valueOf((String) lo.get(0)));
	            vo.setName(String.valueOf(lo.get(1)));
	            vo.setNumber(Integer.valueOf((String) lo.get(2)));
	            System.out.println("打印信息-->书本Id:"+vo.getBookId()+"  名称："+vo.getName()+"   数量："+vo.getNumber());
	            bookDao.insertBook(vo);
	        }
	        return "redirect:/list";
	    }

}

