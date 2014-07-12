<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop - <fmt:message key="registration"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <h3><fmt:message key="registration"/></h3>

        <form action="controller" name="registrationForm" method="post">
            <input type="hidden" name="action" value="registration">
            <fmt:message key="registration.login"/>
            <br>
            <input type="text" name="login" maxlength="20">
            <c:if test="${not empty param.em1}">
                <fmt:message key="${param.em1}"/>
            </c:if><br>
            <fmt:message key="registration.password"/>
            <br>
            <input type="password" name="password" maxlength="25">
            <c:if test="${not empty param.em2}">
                <fmt:message key="${param.em2}"/>
            </c:if><br>
            <fmt:message key="registration.name"/>
            <br>
            <input type="text" name="name" maxlength="40">
            <c:if test="${not empty param.em3}">
                <fmt:message key="${param.em3}"/>
            </c:if><br>
            <fmt:message key="registration.surname"/>
            <br>
            <input type="text" name="surname" maxlength="40">
            <c:if test="${not empty param.em4}">
                <fmt:message key="${param.em4}"/>
            </c:if><br>
            <fmt:message key="registration.address"/>
            <br>
            <textarea rows="3" cols="20" wrap="physical" name="address" maxlength="200"></textarea>
            <c:if test="${not empty param.em5}">
                <fmt:message key="${param.em5}"/>
            </c:if><br>
            <fmt:message key="registration.phoneNumber"/>
            <br>
            <input type="text" name="phoneNumber" maxlength="40">
            <c:if test="${not empty param.em6}">
                <fmt:message key="${param.em6}"/>
            </c:if><br><br>
            <button name="send"><fmt:message key="registration.register"/></button>
        </form>
    </div>
</div>
</body>
</html>
