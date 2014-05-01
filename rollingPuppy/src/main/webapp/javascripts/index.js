function initPage() {
	var COOKIE_EMAIL = "member.lastLoggedEmail";
	var lastLoggedEmail = getCookieValue(COOKIE_EMAIL);
//	var emailToBeChecked = null;
	
	fillEmail(lastLoggedEmail);

	//document.getElementById("join_form").onsubmit = validateJoinEmail;	
	//document.getElementById("login_form").onsubmit = validateLoginEmail;
	

	//윤성소스 추가
	document.querySelector(".c_login").addEventListener('click', loginChoiceONOFF, false);
	document.querySelector(".c_join").addEventListener('click', joinChoiceONOFF, false);
	document.querySelector(".loginArea input[type=submit]").addEventListener('click', login, false);
	document.querySelector(".joinArea input[type=submit]").addEventListener('click', join, false);
	
	//duplicate Email check
	document.getElementById("joinEmail").addEventListener('keyup',duplicateEmail,false);
	
}

/**
 * 인자로 넘겨받은 이름으로 쿠키를 찾아서 value를 반환니다. 
 * @param name
 * @returns value
 */
function getCookieValue(name) {
	var arrayCookieProperties = document.cookie.split(";");
	for (var idx = 0; idx < arrayCookieProperties.length; idx++) {
		var cookieProperty = arrayCookieProperties[idx].trim();
		if (cookieProperty.indexOf(name + "=") === 0) {
			var value = cookieProperty.substring(name.length + 2, cookieProperty.length -1);
			return value;
		}
		
		return null;
	}
}

//마지막으로 로그인 된 email 값을 login email box에 default로 삽입 
function fillEmail(email) {
	if (email == "") {
		return ;
	}
	
	var loginForm = document.forms[0];
	var inputEmail = loginForm.querySelector("input[name=email]");
	
	inputEmail.value = email;
}

//email validation check를 하는 함수
//두 개의 submit 버튼중 어떤 버튼이 눌렸는지 즉, 어느 칸에 입력된 email에 대하여 validation check를 할 것인지 찾는 함수 
//function checkClickedButtonJoin() {
//	var emailToBeChecked = document.getElementById("joinEmail").value;​
//	validateEmail(emailToBeChecked);
//}
//
//function checkClickedButtonLogin() {
//	var emailToBeChecked = document.getElementById("loginEmail").value;​
//	validateEmail(emailToBeChecked);
//}

//input tag의 name 이 email인 곳에 email형식에 맞게 input이 들어왔는지 정규 표현식을 이용해 확인하는 함수 
function isValidateEmailFormat(email) {
	var emailFormat = /^[a-zA-Z0-9\-_]+(\.[a-zA-Z0-9\-_]+)*@[a-z0-9]+(\-[a-z0-9]+)*(\.[a-z0-9]+(\-[a-z0-9]+)*)*\.[a-z]{2,4}$/;
	if (emailFormat.test(email)) {
		return true;
	} else {
		return false;
	}
}

//****************************************************윤성소스 추가 시작
function getStyleValue(node, style) {
	var totalStyle= window.getComputedStyle(node , null);
	return totalStyle.getPropertyValue(style);
}

function loginChoiceONOFF() {
	event.preventDefault();
	
	var loginNode = document.querySelector(".loginArea");
	var loginNodeStyle = getStyleValue(loginNode, "display");
	var titleNode = document.querySelector(".title");
	titleNode.innerHTML = "Welcome. Please login.";
	titleNode.nextElementSibling.innerHTML = "Welcome My Neighbor";
	
	
	if ( loginNodeStyle == "none" )
		loginNode.style.display="block";
	else
		loginNode.style.display="none";

	loginNode.nextElementSibling.style.display="none";
}

function joinChoiceONOFF() {
	event.preventDefault();
	
	var loginNode = document.querySelector(".joinArea");
	var loginNodeStyle = getStyleValue(loginNode, "display");
	var titleNode = document.querySelector(".title");
	
	titleNode.innerHTML = "Be My Neighbor";
	titleNode.nextElementSibling.innerHTML = "And Chat On The Map";
	
	if ( loginNodeStyle == "none" )
		loginNode.style.display="block";
	else
		loginNode.style.display="none";

	loginNode.previousElementSibling.style.display="none";
}

function login(event) {
	event.preventDefault();
	
	var form = event.currentTarget.form;
	
	//check input value
	var email = form[0].value;
	var password = form[1].value;
	
	if ( email.length ===0 || password.length ===0 ) {
		alert ("아이디와 패스워드를 모두 입력해 주세요");
		return;
	}
	
	if ( ! isValidateEmailFormat(email) ) {
		alert("이메일 형식에 맞춰 정확하게 입력해 주세요.")
		return;		
	}
	//check input value END
	
	var formData = new FormData(form);
	var url = "/login";
	var request = new XMLHttpRequest();
	request.open("POST", url, true);
	request.onreadystatechange = function() {
		if (request.readyState == 4 && request.status == 200) {
			var oResult = JSON.parse(request.responseText);
			
			var result = oResult['ThreeWayResult'];
			console.log(result);
			
			if ( result === "SUCCESS" ) {
				alert("\""+oResult["nickname"] +"\" 님 환영합니다.");
				window.location = "/main";
			} else if ( result === "FAIL" ) {
				alert("아이디와 비밀번호를 다시 확인해 주세요.");
			} else if ( result === "UNEXPECTED_ERROR"){ 
				alert("예기치 못한 에러가 발생하였습니다.\n다시 시도해 주세요.");
			} else {
				alert("비정상적 접근입니다.");
			}
		}
	}
	request.send(formData);
}

function join(event) {
	event.preventDefault();

	var form = event.currentTarget.form;
	
	//check input value
	var email = form[0].value;
	var password = form[1].value;
	var passwordR = form[2].value;
	
	if ( email.length===0 || password.length===0 || passwordR===0 ) {
		alert ("공란은 허용되지 않습니다. 모두 입력해 주세요");
		return;
	}
	
	if ( ! isValidateEmailFormat(email) ) {
		alert("이메일 형식에 맞춰 정확하게 입력해 주세요.")
		return;		
	}
	
	if ( password != passwordR ) {
		alert ('입력된 비밀번호가 서로 다릅니다."');
		return;
	}
	//check input value END
	
	
	var formData = new FormData(form);
	var url = "/join";
	var request = new XMLHttpRequest();
	request.open("POST", url, true);
	request.onreadystatechange = function() {
		if (request.readyState == 4 && request.status == 200) {
			var oResult = JSON.parse(request.responseText);
			var isSuccess = oResult['isSuccess'];
			if ( result === true ) {
				alert("이웃님. 반갑습니다.\n초기 닉네임은 자동설정됩니다. ^^\n")
				window.location = "/";
			} else {
				alert("예기치 못한 에러로 회원가입에 실패했습니다.\n다시 시도해 주세요.");
			}
		}
	}
	request.send(formData);
}

//****************************************************윤성소스 추가 끝

//id 중복 체크를 위한 함수 
function duplicateEmail(){
	console.log("check!!");
	var newbieEmail = document.getElementById("joinEmail").value;
	var url = "/join?email="+newbieEmail;
	
	var request = new XMLHttpRequest();
	request.open("GET", url , true );
	request.onreadystatechange = function(){
		if ( request.readyState === 4 && request.status === 200 ) {
			var isExisted = request.responseText;
			console.log(	isExisted);
			console.log(isExisted == 'true' );
			console.log(isExisted == "true" );
			console.log(isExisted === "true" );
			
			if( isExisted == true){
				alert("already exist");
			}
		}
	}
	request.send(); //request를 보내는것 callback 함수당 
}



window.onload = initPage;
