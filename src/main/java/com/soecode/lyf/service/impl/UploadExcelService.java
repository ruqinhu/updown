package com.soecode.lyf.service.impl;


import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import com.soecode.lyf.dao.BookDao;
import com.soecode.lyf.entity.Book;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadExcelService {

    @Autowired
    private BookDao bookDAO;
    
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel
    public void export(String[] titles, ServletOutputStream out) throws Exception {                
        try{
            // 第一步，创建一个workbook，对应一个Excel文件
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
            HSSFSheet hssfSheet = workbook.createSheet("sheet1");
            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
            HSSFRow hssfRow = hssfSheet.createRow(0);
            // 第四步，创建单元格，并设置值表头 设置表头居中
            HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
            //居中样式
            hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            HSSFCell hssfCell = null;
            for (int i = 0; i < titles.length; i++) {
                hssfCell = hssfRow.createCell(i);//列索引从0开始
                hssfCell.setCellValue(titles[i]);//列名1
                hssfCell.setCellStyle(hssfCellStyle);//列居中显示                
            }
            
            // 第五步，写入实体数据 
            List<Book> books = bookDAO.queryAllWithoutParam();            

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            if(books != null && !books.isEmpty()){
                for (int i = 0; i < books.size(); i++) {
                    hssfRow = hssfSheet.createRow(i+1);                
                    Book book = books.get(i);
                    
                    // 第六步，创建单元格，并设置值
                    long userid = 0;
                    if(book.getBookId() != 0){
                        userid = book.getBookId();
                    }
                    hssfRow.createCell(0).setCellValue(userid);
                    String username = "";
                    if(book.getName() != null){
                        username = book.getName();
                    }
                    hssfRow.createCell(1).setCellValue(username);
                 
                    int number = 0;
                    if(book.getNumber() != 0){
                    	number = book.getNumber();
                    }
                    hssfRow.createCell(2).setCellValue(number);
                }
            }
            
            // 第七步，将文件输出到客户端浏览器
            try {
                workbook.write(out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("导出信息失败！");
        }    
    }
    
    
    
    /** 
     * 描述：获取IO流中的数据，组装成List<List<Object>>对象 
     * @param in,fileName 
     * @return 
     * @throws IOException  
     */  
    public  List<List<Object>> getBankListByExcel(InputStream in,String fileName) throws Exception{  
        List<List<Object>> list = null;  
        System.out.println("getBankListByExcel");
        //创建Excel工作薄  
        Workbook work = this.getWorkbook(in,fileName);  
        if(null == work){  
            throw new Exception("创建Excel工作薄为空！");  
        }  
        Sheet sheet = null;  
        Row row = null;  
        Cell cell = null;  
          
        list = new ArrayList<List<Object>>();  
        //遍历Excel中所有的sheet  
        System.out.println("work.getNumberOfSheets()");
        System.out.println(work.getNumberOfSheets());
        for (int i = 0; i < work.getNumberOfSheets(); i++) {  
            sheet = work.getSheetAt(i);  
            if(sheet==null){continue;}  
              
            //遍历当前sheet中的所有行  
            for (int j = sheet.getFirstRowNum()+1; j < sheet.getLastRowNum()+1; j++) {  
                row = sheet.getRow(j);  
                System.out.println(" row = sheet.getRow(j);");
//                if(row==null||row.getFirstCellNum()==j){continue;}  
                  
                //遍历所有的列  
                List<Object> li = new ArrayList<Object>();  
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {  
                    cell = row.getCell(y); 
                    System.out.println("cell.getStringCellValue()");
//                    System.out.println(cell.getStringCellValue());
                    li.add(this.getCellValue(cell));  
                }  
                list.add(li);  
            }  
        }  
        return list;  
    }  
    
    /**
     * 描述：对表格中数值进行格式化
     * @param cell
     * @return
     */
    public  Object getCellValue(Cell cell){
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字

        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            value = cell.getRichStringCellValue().getString();
            break;
        case Cell.CELL_TYPE_NUMERIC:
            if("General".equals(cell.getCellStyle().getDataFormatString())){
                value = df.format(cell.getNumericCellValue());
            }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                value = sdf.format(cell.getDateCellValue());
            }else{
                value = df2.format(cell.getNumericCellValue());
            }
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            value = cell.getBooleanCellValue();
            break;
        case Cell.CELL_TYPE_BLANK:
            value = "";
            break;
        default:
            break;
        }
        return value;
    }
    
    /**
     * 描述：根据文件后缀，自适应上传文件的版本 
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public  Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }
}