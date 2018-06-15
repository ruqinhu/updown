<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html>
<head>
<title>图书生成界面</title>
</head>

<body>
	<!-- 遍历Book集合 -->
	<table>
		<tr>
			<th>bookId</th>
			<th>name</th>
			<th>number</th>
		</tr>
		<c:forEach var="lis" items="${list}">
			<tr>
				<td><c:out value="${lis.bookId}" /></td>
				<td><c:out value="${lis.name}" /></td>
				<td><c:out value="${lis.number}" /></td>

			</tr>
		</c:forEach>
	</table>
	<a href="http://localhost:8080/ssm/export">导出excel表格</a>
	
	 <form action="http://localhost:8080/ssm/upload" method="post" enctype="multipart/form-data">  
        选择文件:<input type="file" name="upfile" width="120px">  
        <input type="submit" value="上传">  
    </form> 
</body>
</html>
