<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%! 
	final String SESSION_NICKNAME_NOUN = "member.nickname_noun";
	final String SESSION_NICKNAME_ADJECTIVE = "member.nickname_adjective";
%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Main Page</title>
	<link type="text/css" rel="stylesheet" href="/stylesheets/reset.css">
	<link type="text/css" rel="stylesheet" href="/stylesheets/default.css">
	<link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
	<link type="text/css" rel="stylesheet" href="/stylesheets/map_menu.css">
	<script type="text/javascript" src="http://openapi.map.naver.com/openapi/naverMap.naver?ver=2.0&key=f154abb26c9c79ed5a4a25d000a9349c"></script>
</head>

<body>
<input type="hidden" id="nickname" value="<%=session.getAttribute(SESSION_NICKNAME_ADJECTIVE) + " " + session.getAttribute(SESSION_NICKNAME_NOUN)%>"/>
<!--페이지 전체를 감싸는 영역-->
<div id="wrapper">

	<!--검색박스를 포함하는 헤더 영역-->
	<div id="header">
		<h1><a href="/main">Rolling Puppy</a></h1>
		
		<!--검색 박스와 검색 버튼을 포함하는 영역-->
		<div id="search_box">
			<input type="text"></input>
			<button class="button" type="submit">검색</button>
		</div>
	</div>
	
	<!--헤더를 제외하고 네비게이션바와 지도를 포함하는 핵심 컨텐츠 영역-->
	<div id="container">
	
		<!-- 네이게이션바와 네이게이션 바의 메뉴를 눌렀을 때 나오는 패널을 함께 묶은 영역-->
		<div id="aside">

			<!--네비게이션바 영역 -->
			<div id="nav">
				<ul class="nav_list">
					<li><a href="#" class="search"></a></li>
					<li><a href="#" class="recommendation"></a></li>
					<li><a href="#" class="chatting"></a></li>
					<li><a href="#" class="bookmark"></a></li>
					<li><a href="#" class="settings"></a></li>
				</ul>
			</div>
			
			<!--네비게이션바를 눌렀을때 나오는 패널 영역-->
			<div id="panel">
				<a class="panel_button_close" href="#"></a>
				<a class="panel_button_open" href="#"></a>
			</div>
		</div>
		
		<!-- 컨텐츠 영역에서 네비게이션과 패널 영역을 제외한, 지도를 포함한 영역-->
		<div id="content">
			<div id="map_area">
			
				<!-- 지도 영역 -->
				<div id="naver_map" class="naver_map"></div>
			</div>
		</div>
	</div>
	
</div>
</body>
<script type="text/javascript" src="/javascripts/main.js"></script>
<script type="text/javascript" src="http://127.0.0.1:3080/socket.io/socket.io.js"></script>
<script type="text/javascript" src="/javascripts/map_menu.js"></script>
</html>
