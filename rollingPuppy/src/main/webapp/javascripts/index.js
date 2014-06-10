var boolIsMobile = typeof window.orientation !== "undefined" ? true : false;

var oUtil = {
	//input tag의 name 이 email인 곳에 email형식에 맞게 input이 들어왔는지 정규 표현식을 이용해 확인하는 함수 
	isValidateEmailFormat: function(email) {
		var emailFormat = /^[\w\-]+(\.[\w\-]+)*@[a-z\d]+(\-[a-z\d]+)*(\.[a-z\d]+(\-[a-z\d]+)*)*\.[a-z]{2,4}$/;
		if (emailFormat.test(email)) {
			return true;
		} else {
			return false;
		}
	},
	getStyleValue: function(node, style) {
		var totalStyle= window.getComputedStyle(node , null);
		return totalStyle.getPropertyValue(style);
	}
}

var oSelectBox = {
	eLoginSelector: null,
	eLoginEntireArea: null,
	eJoinSelector: null,
	eTitle: null,
	eComment: null,
	
	changeView: function(targetElement, oppositeElement, titleText, commentText) {
		event.preventDefault();
		
		var targetStyle = oUtil.getStyleValue(targetElement, "display");
		
		this.eTitle.innerText = titleText;
		this.eComment.innerText = commentText;
		
		if ( targetStyle == "none" ) {
			targetElement.style.display = "block";
		} else {
			targetElement.style.display = "none";
		}
		
		oppositeElement.style.display="none";
	},
	
	initialize: function() {
		this.eLoginSelector = document.querySelector(".c_login");
		this.eJoinSelector = document.querySelector(".c_join");
		
		this.eLoginEntireArea = document.querySelector(".loginArea");
		this.eJoinEntireArea = document.querySelector(".joinArea");
		
		this.eTitle = document.querySelector(".title");
		this.eComment = this.eTitle.nextElementSibling;
		
		
		this.eLoginSelector.addEventListener(
				boolIsMobile ? "touchend" : "click",
				function() {
					this.changeView(
							this.eLoginEntireArea,
							this.eJoinEntireArea,
							"Welcome!",
							"Welcome My Neighbor"
					);
				}.bind(this), false);
		
		this.eJoinSelector.addEventListener(
				boolIsMobile ? "touchend" : "click",
				function() {
					this.changeView(
							this.eJoinEntireArea,
							this.eLoginEntireArea,
							"Be My Neighbor",
							"And Chat On The Map"
					);
				}.bind(this), false);
	}	
};

var oLogin = {
	eInputEmail: null,
	eInputPassword: null,
	eCheckboxKeepLogin: null,
	
	//마지막으로 로그인 된 email 값을 login email box에 default로 삽입 
	inputLastLoggedEmail: function(email) {
		if (email === null) {
			return ;
		}
		
		var loginForm = document.forms[0];
		var inputEmail = loginForm.querySelector("input[name=email]");
		var checkboxKeepEmail = loginForm.querySelector("input[name=keepEmail]");
		
		inputEmail.value = email;
		console.log(email);
		checkboxKeepEmail.checked = true;
	},
	
	login: function (event) {
		//event.preventDefault();
		
		var form = event.currentTarget.form;
		
		//check input value
		var email = form[0].value;
		var password = form[1].value;
		
		if ( email.length ===0 || password.length ===0 ) {
			alert ("아이디와 패스워드를 모두 입력해 주세요");
			return;
		}
		
		if ( ! oUtil.isValidateEmailFormat(email) ) {
			alert("이메일 형식에 맞춰 정확하게 입력해 주세요.")
			return;		
		}
		//check input value END
		
		var oParameter = {
			"email": form[0].value,
			"password": form[1].value,
			"keepEmail": form[2].value
		};
	},
	initialize: function() {
		var lastLoggedEmail = oCookie.getEmailCoockieValue();
		this.inputLastLoggedEmail(lastLoggedEmail);
		document.querySelector("#login_button").addEventListener(
				boolIsMobile ? "touchend" : "click", this.login, false);
	}
};

var oJoin = {
	eInputEmail: null,
	eInputPassword: null,
	eInputPasswordConfirm: null,
	checkEmailExsitsAndNoticeUser: function(){
		var duplicateCheckPtag = document.getElementById("duplicateCheck");
		var newbieEmail = document.getElementById("joinEmail").value;
		var url = "/join?email="+newbieEmail;
		if(oUtil.isValidateEmailFormat(newbieEmail)){
			var request = new XMLHttpRequest();
			request.open("GET", url , true );
			request.onload = function(){
				var oResult = JSON.parse(request.responseText);
				var result = oResult["ThreeWayResult"];
				console.log(result);

				if( result === "FAIL" ){
					duplicateCheckPtag.innerText = "이미 존재하는 이메일 입니다.";
				}else if (result === "SUCCESS"){
					duplicateCheckPtag.innerText = "가입 가능한 이메일 입니다."
				}else {
					alert("예기치 못한 사건이 발생했다!!");
				}
			}
			request.send(); //request를 보내는것 callback 함수당 
		} else {
			return;
		}
	},
	join: function (event) {
		var form = event.currentTarget.form;
		
		//check input value
		var email = form[0].value;
		var password = form[1].value;
		var passwordR = form[2].value;	
		
		if ( email.length===0 || password.length===0 || passwordR===0 ) {
			alert ("공란은 허용되지 않습니다. 모두 입력해 주세요");
			return;
		}
		
		if ( ! oUtil.isValidateEmailFormat(email) ) {
			alert("이메일 형식에 맞춰 정확하게 입력해 주세요.")
			return;		
		}
		
		if ( password != passwordR ) {
			alert ('입력된 비밀번호가 서로 다릅니다."');
			return;
		}
		//check input value END
	},
	initialize: function() {
		document.querySelector("#join_button").addEventListener(
				boolIsMobile ? "touchend" : "click", this.join, false);
		
		//duplicate Email check
		document.getElementById("joinEmail").addEventListener('keyup',this.checkEmailExsitsAndNoticeUser, false);
	}
};

var oCookie = {
	/**
	 * 인자로 넘겨받은 이름으로 쿠키를 찾아서 value를 반환니다. 
	 * @param name
	 * @returns value
	 */
	getCookieValue: function(name) {
		var arrayCookieProperties = document.cookie.split(";");
		
		for (var idx = 0; idx < arrayCookieProperties.length; idx++) {
			var cookieProperty = arrayCookieProperties[idx].trim();
			if (cookieProperty.indexOf(name + "=") === 0) {
				var value = cookieProperty.substring(name.length + 2, cookieProperty.length -1);
				return value;
			}
			
			return null;
		}
	},
	
	getEmailCoockieValue: function() {
		var COOKIE_EMAIL = "member.lastLoggedEmail";
		return this.getCookieValue(COOKIE_EMAIL)
	}
};

function initialize() {
	oSelectBox.initialize();
	oLogin.initialize();
	oJoin.initialize();
}
