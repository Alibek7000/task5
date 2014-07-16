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
    <title>BikeShop - <fmt:message key="editingAnItem"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/jsp/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <form action="controller" name="editItemFrom" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" value="editItem">
            <fmt:message key="table.name"/>
            <br>
            <textarea name="name" rows="3" cols="20"
                      wrap="physical">${sessionScope.editItem.getName()}</textarea>
            <c:if test="${not empty param.em1}">
                <fmt:message key="${param.em1}"/>
            </c:if><br>
            <fmt:message key="table.description"/>
            <br>
            <textarea rows="10" cols="50" wrap="physical"
                      name="description">${sessionScope.editItem.getDescription()}</textarea>
            <c:if test="${not empty param.em2}">
                <fmt:message key="${param.em2}"/>
            </c:if><br>
            <fmt:message key="quantity"/>
            <br>
            <input type="text" name="quantity" maxlength="10" value="${sessionScope.editItemQuantity}">
            <c:if test="${not empty param.em3}">
                <fmt:message key="${param.em3}"/>
            </c:if><br>
            <fmt:message key="table.price"/>
            <br>
            <input type="text" name="price" value="${sessionScope.editItem.getPrice()}">
            <c:if test="${not empty param.em4}">
                <fmt:message key="${param.em4}"/>
            </c:if><br>
            <select name="categoryId" onchange="">
                <option value="${sessionScope.editItem.getCategory()}">old value</option>
                <option value="0">without category</option>
                <c:forEach items="${sessionScope.categories}" var="category">
                    <option value="${category.getId()}">${category.getName()}</option>
                </c:forEach>
            </select>
            <br><fmt:message key="uploadImage"/>: <input type="file" name="file"/><br>
            <button name="send"><fmt:message key="table.edit"/></button>
        </form>
        <c:if test="${not empty param.errorMessage}">
            <fmt:message key="${param.errorMessage}"/>
        </c:if>
    </div>
</div>
</body>
</html>
