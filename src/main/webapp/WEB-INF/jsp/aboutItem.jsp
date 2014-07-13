<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop - <fmt:message key="title.aboutItem"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/jsp/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <c:set var="item" value="${sessionScope.item}"/>
        <h2><c:out value="${item.getName()}"/></h2>
        <c:set var="pathToImage" value="${sessionScope.pathToImages}${item.getId()}.png"/>
        <div id="pic">
            <img src="D:/shop/images/items/${item.getId()}.png" alt="" width="250" tabindex="0" class="zoom-images"/>
        </div>
        <img src="${pathToImage}"  width="250"/>
        ${item.getDescription()}
        <br>
        <fmt:formatNumber var="price" value="${item.getPrice()}"
                          maxFractionDigits="2"/>
        <td><strong><fmt:message key="table.price"/> $<c:out value="${price}"/></strong></td>
        <br>

        <form action="controller" name="buyItemForm" method="post">
            <fmt:message key="quantity"/>
            <div id="digitField"><input type="text" name="quantity" value="1"></div>
            &nbsp;
            <input type="hidden" name="action" value="buyItemButton">
            <input type="hidden" name="id" value="${item.getId()}">
            <input type="hidden" name="item" value="${item}">
            <button name="send"><fmt:message key="button.addToBasket"/></button>
        </form>
        <br>
        <c:if test="${not empty param.errorMessage}">
            <fmt:message key="${param.errorMessage}"/>
        </c:if>
        <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
            <td>
                <button onClick="location.href='controller?action=showEditItemPage&id=${item.getId()}'"><fmt:message
                        key="table.edit"/>
                </button>
            </td>
            <td>
                <button onClick="location.href='controller?action=removeItemFromBase&id=${item.getId()}'"><fmt:message
                        key="table.remove"/>
                </button>
            </td>
        </c:if>
        ${pathToImage}
    </div>
</div>
</html>
