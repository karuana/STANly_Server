var popupmenu_info = [
	{title: "Fat", content: "코드에서 객체간 연관 정도를 보여주는 지표이다."},
	{title: "Coupling", content: "커플링 관련 지표이다."},
	{title: "Naming", content: "변수나 객체의 작명 규칙 위반의 정도를 보여주는 지표이다."},
	{title: "Basic Rule", content: "PMD를 통해 나온 정보로 규칙 위반의 정도를 보여주는 지표이다."}
];

function supportsSVG() {
	return !!document.createElementNS && !!document.createElementNS('http://www.w3.org/2000/svg', "svg").createSVGRect && !!(window.SVGSVGElement);
}

function rotateObject(obj, deg)
{
	$(obj).css("transform", "rotate("+deg+"deg)")
		.css("-moz-transform", "rotate("+deg+"deg)")
		.css("-webkit-transform", "rotate("+deg+"deg)")
		.css("-ms-transform", "rotate("+deg+"deg)")
		.css("-o-transform", "rotate("+deg+"deg)");
}

function resizeEvent(obj)
{
	$("#content").css("min-height", "0px");
	if($("#content").height() > $(window).height() - 74)
	{
		$("#sidebar-container").css("min-height", $("#content").height() );
	}
	else
	{
		$("#content").css("min-height", $(window).height() - 74);
		$("#sidebar-container").css("min-height", $(window).height() - 74);
	}
}

window.onload = resizeEvent;
$(document).ready(function() {
	resizeEvent();

	$("#user-container").hover(function(){$(this).addClass("up")}, function(){$(this).removeClass("up")});
	
	$("#container").addClass("container-js");
	if(supportsSVG()) $("#container").addClass("svg");
	if(navigator.userAgent.match(/applewebkit/i))
	{
		if(!navigator.geolocation)
		{
			$("#container").addClass("decelerate");
		}
		else
		{
			if (navigator.platform.match(/ipad/i) ||
				navigator.platform.match(/iphone/i) ||
				navigator.platform.match(/ipod/i) ) $("#container").addClass("ios");
		}
		if (navigator.userAgent.match(/chrome/i) &&
			navigator.userAgent.match(/windows/i) ) $("#container").addClass("noinset");
	}
	window.onresize = resizeEvent;


	$(".left-hidden-menu .right-button").click(function() {
		if($(".left-hidden-menu").css("margin-left") == "-210px")
		{
			$(".left-hidden-menu").stop(true, true);
			$(".right-content-area").stop(true, true);
			$(".left-hidden-menu").animate({ 'margin-left':0 }, 250, function() { $(this).css('margin-left', 0); } );
			$(".right-content-area").animate({ 'margin-right':-211 }, 250, function() { $(this).css('margin-right', "-210px"); } );
		}
		else
		{
			$(".left-hidden-menu").stop(true, true);
			$(".right-content-area").stop(true, true);
			$(".left-hidden-menu").animate({ 'margin-left':-210 }, 250, function() { $(this).css('margin-left', "-210px"); } );
			$(".right-content-area").animate({ 'margin-right':0 }, 250, function() { $(this).css('margin-right', 0); } );
		}
	});
});