<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop - <fmt:message key="categories"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <table border="1">
            <tr>
                <th>Id</th>
                <th><fmt:message key="table.name"/></th>
                <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
                    <th><fmt:message key="table.parentId"/></th>
                    <th><fmt:message key="table.edit"/></th>
                    <th><fmt:message key="table.remove"/></th>
                </c:if>
            </tr>
            <c:forEach items="${sessionScope.categories}" var="category">
                <tr>
                    <td><c:out value="${category.getId()}"/></td>
                    <td>
                        <a href="controller?action=showCategoryItems&parentId=${category.getParentId()}&categoryId=${category.getId()}"><c:out
                                value="${category.getName()}"/></a></td>
                    <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
                        <td><c:out value="${category.getParentId()}"/></td>
                        <td>
                            <button onClick="location.href='controller?action=showEditCategoryPage&id=${category.getId()}'">
                                <fmt:message key="table.edit"/>
                            </button>
                        </td>
                        <td>
                            <button onClick="location.href='controller?action=removeCategory&id=${category.getId()}'">
                                <fmt:message key="table.remove"/>
                            </button>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>

        </table>
        <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
            <button onClick="location.href='controller?action=addCategory'"><fmt:message key="button.add"/></button>
        </c:if>
    </div>
</div>
</body>
</html>
