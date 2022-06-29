<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="section" value="0" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 조회하기</title>
<style>

div#maintitle {
    text-align: center;
}

tr#top-table {
    height: 30px;
}

div .border-bottom {
    text-align: center;
}

div .lilili {
	margin: 0px 80px 0px 80px;
    height: 300px;
    display: inline-flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;
}
.api {
width: 400px;
height: 600px;
margin: auto;
}

textarea {
width: 1200px;
height: 205px;
font-size: 20px;
text-align: left;
}

</style>
</head>
<body>
<div class="main-container">
				<div id=maintitle><h1>밀키트 판매, 중개 사이트 입니다</h1></div>
				
				<img src="" class = "api">
				<img src="" class = "api">
				<img src="" class = "api">
				<div class="table-container">
					<div id="stable-striped">
					 어드민 아이디: admin
					 비밀번호: 123
					 <h2>개발환경</h2>
					 
					 <h3>FrontEnd</h3>
					 Language: HTML5, CSS, JavaScript<br>
					 Framework: Jquery 1.12.4, JSON, Ajax
					 <h3>BackEnd</h3>
					 Language: Java 15.0.2, JSP<br>
					 Framework: MyBatis, Spring Framework
					 <br>
					 <a href = "${contextPath}/main/main.do">사이트로 진입하기</a>
					</div>
					

				</div>
			</div>
</body>
</html>