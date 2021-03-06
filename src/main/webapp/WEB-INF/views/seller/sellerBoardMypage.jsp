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
<script>
	function sortTable(n) {
		var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
		table = document.getElementById("stable-striped");
		switching = true;
		//Set the sorting direction to ascending:
		dir = "asc";
		/*Make a loop that will continue until
		no switching has been done:*/
		while (switching) {
			//start by saying: no switching is done:
			switching = false;
			rows = table.rows;
			/*Loop through all table rows (except the
			first, which contains table headers):*/
			for (i = 1; i < (rows.length - 1); i++) {
				//start by saying there should be no switching:
				shouldSwitch = false;
				/*Get the two elements you want to compare,
				one from current row and one from the next:*/
				x = rows[i].getElementsByTagName("TD")[n];
				y = rows[i + 1].getElementsByTagName("TD")[n];
				/*check if the two rows should switch place,
				based on the direction, asc or desc:*/
				if (dir == "asc") {
					if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
						//if so, mark as a switch and break the loop:
						shouldSwitch = true;
						break;
					}
				} else if (dir == "desc") {
					if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
						//if so, mark as a switch and break the loop:
						shouldSwitch = true;
						break;
					}
				}
			}
			if (shouldSwitch) {
				/*If a switch has been marked, make the switch
				and mark that a switch has been done:*/
				rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
				switching = true;
				//Each time a switch is done, increase this count by 1:
				switchcount++;
			} else {
				/*If no switching has been done AND the direction is "asc",
				set the direction to "desc" and run the while loop again.*/
				if (switchcount == 0 && dir == "asc") {
					dir = "desc";
					switching = true;
				}
			}
		}
	}
</script>
<style type="text/css">
.div1 {
	width: 1080px;
	height: 1000px;
	position: relative;
	overflow: hidden;
}

.div2 {
	width: 335px;
	height: 100px;
	background: #6DB800;
	text-align: center;
	font-size: 50px;
	line-height: 100px;
	margin-left: 33%;
	display: inline-block
}

.div2-1 {
	display: inline-block;
	float: right;
	margin-right: 10%;
	margin-top: 20px;
	font-size: 20px;
}

.div2-1 a {
	text-decoration: none;
	background: #6DB800;
	color: black;
	border: 3px solid gray;
}

.div3 {
	margin-top: 10px;
	margin-left: 10%;
	width: 80%;
	height: 100px;
	font-size: 45px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	border: 5px solid #ffd3dd;
}

.div3 div {
	display: inline-flex;
}

.div3 .div3-1 {
	font-size: 30px;
}

.div3 .div3-2 {
	font-size: 15px;
}

.div3 .div3-3 {
	font-size: 15px;
}

.div4 {
	display: flex;
	background: white;
	height: 150px;
	width: 100%;
	flex-wrap: nowrap;
	justify-content: space-around;
}

.div4 ul {
	list-style: none;
	padding-inline-start: 0px;
}

.div4 .tabmenu {
	width: 100%;
	margin: 0 auto;
	position: relative;
	margin: 0 auto;
}

.div4 .tabmenu ul li {
	display: inline-block;
	width: 300px;
	height: auto;
	text-align: center;
	background: #ffd3dd;
	margin: 0 27px;
}

.div4 .tabmenu label {
	display: block;
	width: 100%;
	height: 100px;
	font-size: 35px;
}

.div4 .tabmenu #tabmenu1 {
	display: none;
}

.div4 .tabmenu #tabmenu2 {
	display: none;
}

.div4 .tabmenu #tabmenu3 {
	display: none;
}

.div4 .tabCon {
	display: none;
	text-align: left;
	left: 0;
	top: 80%;
	position: absolute;
	box-sizing: border-box;
	width: 100%;
	height: 800px;
	border: 5px solid #6DB800;
}

.div4 .tabmenu input:checked ~ label {
	background: #ff69b45e;
}

.div4 .tabmenu input:checked ~ .tabCon {
	display: block;
}

tr.border-bottom {
	text-align: center;
}

thead#tap-head {
	text-align: center;
}

th.header {
	padding-left: 6px;
	border-bottom: 5px solid #6DB800
}

.table-container {
	font-size: 14px;
	font-weight: 400;
}

td {
	border-bottom: 1px solid black;
}

.footer-wrap {
	margin-top: 10px;
}

td.fixed {
	text-align: center;
}

span#fa\ fa-star\ checked {
	float: left;
	color: aqua;
	width: 30px;
	height: 30px;
}

span#fa\ fa-star\ {
	float: left;
	width: 30px;
	height: 30px;
}
.checked {
	color: #6DB800;
	font-size: 60px;
}
</style>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<form action="url" method="post" enctype="multipart/form-data">
		<div class="div1">
			<div class="div3">
				<div class="div3-1">${sellerVO.s_Wname }</div>
				<div class="div3-2">${sellerVO.s_id }</div>
				<div class="div3-3">
					이번달 판매금액 <br> 총 판매 금액
				</div>
			</div>
			<div class="div4">
				<div class="tabmenu">
					<ul>
						<li id="tab1" class="btnCon"><input type="radio" checked
							name="tabmenu" id="tabmenu1"> <label for="tabmenu1"><br>상품문의
						</label>
							<div class="tabCon">
								<div class="main-container">
									<div class="table-container">
										<table id="stable-striped">
											<thead id="tap-head">
												<tr id="top-table">
													<th onclick="sortTable(0)" class="header" width="50px">번호</th>
													<th onclick="sortTable(1)" class="header" width="150px">작성자</th>
													<th onclick="sortTable(2)" class="header" width="300px">글
														제목</th>
													<th onclick="sortTable(3)" class="header" width="150px">작성일자</th>
													<th onclick="sortTable(4)" class="header" width="50px">비밀글</th>
													<th onclick="sortTable(5)" class="header" width="250px">상품이름</th>
													<th class="header" width="100px">답글작성</th>
												</tr>
											</thead>
											<c:choose>
												<c:when test="${empty boardGqSellerList}">
													<tr>
														<td colspan=7 class="fixed"><strong>등록된 상품이
																없습니다.</strong></td>
													</tr>
												</c:when>
												<c:when test="${not empty boardGqSellerList}">
													<c:forEach var="item" items="${boardGqSellerList}">
														<tr class="border-bottom">
															<td>${item.b_gq_id}</td>
															<td>${item.u_id}</td>
															<td><a
																href="${contextPath}/boardGq/gq_detail.do?b_gq_id=${item.b_gq_id}">${item.title}</a></td>
															<td>${item.creDate}</td>
															<c:if test="${item.secret == null}">
																<td>N</td>
															</c:if>
															<c:if test="${item.secret != null }">
																<td>${item.secret}</td>
															</c:if>
															<td>${item.g_name}</td>
															<c:if test="${item.compare == 'N' }">
																<td><a
																	href="${contextPath}/boardGq/boardGqReviewform.do?b_gq_id=${item.b_gq_id}">답글작성</a></td>
															</c:if>
															<c:if test="${item.compare == 'Y' }">
																<td><p>답변완료</p></td>
															</c:if>
														</tr>
													</c:forEach>
												</c:when>
											</c:choose>
										</table>
										<center>
											<div class="" id="pagination">
												<c:forEach var="page" begin="1" end="10" step="1">
													<c:if test="${section >0 && page==1 }">
														<a
															href="${contextPath}/seller/sellerBoardMypage.do?section=${section}-1&pageNum=${(section-1)*10+1 }">preview</a>
													</c:if>
													<a
														href="${contextPath}/seller/sellerBoardMypage.do?section=${section}&pageNum=${page}">${(section)*10 +page}
													</a>
													<c:if test="${page == 10 }">
														<a
															href="${contextPath}/seller/sellerBoardMypage.do?section=${section}+1&pageNum=${section*10}+1">다음</a>
													</c:if>
												</c:forEach>
											</div>
										</center>

									</div>
								</div>
							</div></li>
						<li id="tab2" class="btnCon"><input type="radio"
							name="tabmenu" id="tabmenu2"> <label for="tabmenu2"><br>상품후기
						</label>
							<div class="tabCon">
								<div class="main-container">
									<div class="table-container">
										<table id="stable-striped">
											<thead id="tap-head">
												<tr id="top-table">
													<th onclick="sortTable(0)" class="header" width="50px">번호</th>
													<th onclick="sortTable(1)" class="header" width="100px">작성자</th>
													<th onclick="sortTable(2)" class="header" width="300px">글
														제목</th>
													<th onclick="sortTable(3)" class="header" width="150px">작성일자</th>
													<th onclick="sortTable(4)" class="header" width="50px">비밀글</th>
													<th onclick="sortTable(5)" class="header" width="204px">상품이름</th>
													<th onclick="sortTable(6)" class="header" width="200px">평점</th>
													<th class="header" width="100px">답글작성</th>
												</tr>
											</thead>
											<c:choose>
												<c:when test="${empty boardGrSellerList}">
													<tr>
														<td colspan=8 class="fixed"><strong>등록된 상품이
																없습니다.</strong></td>
													</tr>
												</c:when>
												<c:when test="${not empty boardGrSellerList}">
													<c:forEach var="item" items="${boardGrSellerList}">
														<tr class="border-bottom">
															<td>${item.b_gr_id}</td>
															<td>${item.u_id}</td>
															<td><a
																href="${contextPath}/boardGr/gr_detail.do?b_gr_id=${item.b_gr_id}">${item.title}</a></td>
															<td>${item.creDate}</td>
															<c:if test="${item.secret == null}">
																<td>N</td>
															</c:if>
															<c:if test="${item.secret != null }">
																<td>${item.secret}</td>
															</c:if>
															<td>${item.g_name}</td>
															<td><c:choose>
																	<c:when test="${item.star == 5}">
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																	</c:when>
																	<c:when test="${item.star == 4}">
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star"></span>
																	</c:when>
																	<c:when test="${item.star == 3}">
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star"></span>
																		<span class="fa fa-star"></span>
																	</c:when>
																	<c:when test="${item.star == 2}">
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star"></span>
																		<span class="fa fa-star"></span>
																		<span class="fa fa-star"></span>
																	</c:when>
																	<c:otherwise>
																		<span class="fa fa-star checked"></span>
																		<span class="fa fa-star"></span>
																		<span class="fa fa-star"></span>
																		<span class="fa fa-star"></span>
																		<span class="fa fa-star"></span>
																	</c:otherwise>
																</c:choose></td>
															<c:if test="${item.compare == 'N' }">
																<td><a
																	href="${contextPath}/boardGr/boardGrReviewform.do?b_gr_id=${item.b_gr_id}">답글작성</a></td>
															</c:if>
															<c:if test="${item.compare == 'Y' }">
																<td><p>답변완료</p></td>
															</c:if>
														</tr>
													</c:forEach>
												</c:when>
											</c:choose>
										</table>
										<center>
											<div class="" id="pagination">
												<c:forEach var="page" begin="1" end="10" step="1">
													<c:if test="${section >0 && page==1 }">
														<a
															href="${contextPath}/seller/sellerMypage.do?section=${section}-1&pageNum=${(section-1)*10+1 }">preview</a>
													</c:if>
													<a
														href="${contextPath}/seller/sellerMypage.do?section=${section}&pageNum=${page}">${(section)*10 +page}
													</a>
													<c:if test="${page == 10 }">
														<a
															href="${contextPath}/seller/sellerMypage.do?section=${section}+1&pageNum=${section*10}+1">다음</a>
													</c:if>
												</c:forEach>
											</div>
										</center>

									</div>
								</div>
							</div></li>
						<li id="tab3" class="btnCon"><input type="radio"
							name="tabmenu" id="tabmenu3"> <label for="tabmenu3"><br>1대1문의
						</label>
							<div class="tabCon">
								<div class="main-container">
									<div class="table-container">
										<table id="stable-striped">
											<thead id="tap-head">
												<tr id="top-table">
													<th onclick="sortTable(0)" class="header" width="216px">작성번호</th>
													<th onclick="sortTable(1)" class="header" width="216px">작성자</th>
													<th onclick="sortTable(2)" class="header" width="216px">글
														제목</th>
													<th onclick="sortTable(3)" class="header" width="216px">작성일자</th>
													<th onclick="sortTable(4)" class="header" width="216px">비밀글</th>
													<th class="header" width="180px">답글작성</th>
												</tr>
											</thead>
											<c:choose>
												<c:when test="${empty board1SellerList}">
													<tr>
														<td colspan=6 class="fixed"><strong>등록된 상품이
																없습니다.</strong></td>
													</tr>
												</c:when>
												<c:when test="${not empty board1SellerList}">
													<c:forEach var="item" items="${board1SellerList}">
														<tr class="border-bottom">
															<td>${item.b_1_id}</td>
															<td>${item.u_id}</td>
															<td><a
																href="${contextPath}/board1/b1Detail.do?b_1_id=${item.b_1_id}">${item.title}</a></td>
															<td>${item.creDate}</td>
															<c:if test="${item.secret == null}">
																<td>N</td>
															</c:if>
															<c:if test="${item.secret != null }">
																<td>${item.secret}</td>
															</c:if>
															<c:if test="${item.compare == 'N' }">
																<td><a
																	href="${contextPath}/board1/board1Reviewform.do?b_gq_id=${item.b_1_id}">답글작성</a></td>
															</c:if>
															<c:if test="${item.compare == 'Y' }">
																<td><p>답변완료</p></td>
															</c:if>
														</tr>
													</c:forEach>
												</c:when>
											</c:choose>
										</table>
										<center>
											<div class="" id="pagination">
												<c:forEach var="page" begin="1" end="10" step="1">
													<c:if test="${section >0 && page==1 }">
														<a
															href="${contextPath}/seller/sellerMypage.do?section=${section}-1&pageNum=${(section-1)*10+1 }">preview</a>
													</c:if>
													<a
														href="${contextPath}/seller/sellerMypage.do?section=${section}&pageNum=${page}">${(section)*10 +page}
													</a>
													<c:if test="${page == 10 }">
														<a
															href="${contextPath}/seller/sellerMypage.do?section=${section}+1&pageNum=${section*10}+1">다음</a>
													</c:if>
												</c:forEach>
											</div>
										</center>

									</div>
								</div>
							</div></li>
					</ul>
				</div>
			</div>

		</div>
	</form>
</body>
</html>