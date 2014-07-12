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
    <title>BikeShop - <fmt:message key="title.addCategory"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <fmt:message key="title.addCategory"/>
        <form action="controller" name="addCategoryFrom" method="post">
            <input type="hidden" name="action" value="addCategory">
            <fmt:message key="table.name"/>
            <br>
            <input type="text" name="name"><br>
            <fmt:message key="category.nameOnRu"/>
            <br>
            <input type="text" name="ruName"><br>
            <fmt:message key="table.parentId"/>
            <br>
            <select name="parentId" onchange="">
                <option value="">without category</option>
                <c:forEach items="${sessionScope.categories}" var="category">
                    <c:if test="${category.getParentId() == 0}">
                        <option value="${category.getId()}">${category.getName()}</option>
                    </c:if>
                </c:forEach>
            </select>

            <br>
            <button name="send"><fmt:message key="button.add"/></button>
        </form>


        <c:if test="${not empty param.errorMessage}">
            <fmt:message key="${param.errorMessage}"/>
        </c:if>
    </div>
</div>
</body>
</html>
