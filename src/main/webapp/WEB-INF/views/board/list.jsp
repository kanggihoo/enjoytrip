<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${type=='notice'}">공지사항</c:when><c:otherwise>자유게시판</c:otherwise></c:choose> - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 900px; margin: 40px auto; padding: 0 16px; }
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .page-header h2 { color: #1a73e8; font-size: 24px; }
        .tab-bar { display: flex; gap: 4px; margin-bottom: 20px; }
        .tab { padding: 8px 20px; border-radius: 6px 6px 0 0; text-decoration: none; font-size: 14px; font-weight: 600; border: 1px solid #ddd; background: #fff; color: #666; }
        .tab.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
        .board-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 6px rgba(0,0,0,.08); }
        .board-table th { background: #f8f9fa; padding: 12px 16px; font-size: 13px; color: #555; text-align: left; border-bottom: 2px solid #e9ecef; }
        .board-table td { padding: 12px 16px; font-size: 14px; border-bottom: 1px solid #f0f0f0; }
        .board-table tr:last-child td { border-bottom: none; }
        .board-table tr:hover td { background: #f8f9ff; }
        .title-link { color: #222; text-decoration: none; font-weight: 500; }
        .title-link:hover { color: #1a73e8; }
        .notice-badge { background: #e8f0fe; color: #1a73e8; font-size: 11px; font-weight: 700; padding: 2px 6px; border-radius: 4px; margin-right: 6px; }
        .btn-write { padding: 9px 20px; background: #1a73e8; color: #fff; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; text-decoration: none; }
        .btn-write:hover { background: #1557b0; }
        .empty { text-align: center; padding: 60px; color: #aaa; }
        .num { color: #888; font-size: 13px; }
        .date { color: #aaa; font-size: 12px; }
        .views { color: #aaa; font-size: 12px; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <div class="tab-bar">
        <a href="${pageContext.request.contextPath}/board/list?type=notice"
           class="tab ${type=='notice' ? 'active' : ''}">공지사항</a>
        <a href="${pageContext.request.contextPath}/board/list?type=free"
           class="tab ${type=='free' ? 'active' : ''}">자유게시판</a>
    </div>

    <div class="page-header">
        <h2><c:choose><c:when test="${type=='notice'}">공지사항</c:when><c:otherwise>자유게시판</c:otherwise></c:choose></h2>
        <c:if test="${not empty sessionScope.loginUser}">
            <a href="${pageContext.request.contextPath}/board/write?type=${type}" class="btn-write">글쓰기</a>
        </c:if>
    </div>

    <table class="board-table">
        <thead>
            <tr>
                <th style="width:60px;">번호</th>
                <th>제목</th>
                <th style="width:100px;">작성자</th>
                <th style="width:110px;">작성일</th>
                <th style="width:60px;">조회</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty boardList}">
                    <tr><td colspan="5" class="empty">등록된 게시글이 없습니다.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="b" items="${boardList}" varStatus="s">
                        <tr>
                            <td class="num">${boardList.size() - s.index}</td>
                            <td>
                                <c:if test="${type=='notice'}"><span class="notice-badge">공지</span></c:if>
                                <a class="title-link"
                                   href="${pageContext.request.contextPath}/board/detail?boardId=${b.boardId}&type=${type}">
                                    ${b.title}
                                </a>
                            </td>
                            <td>${b.userId}</td>
                            <td class="date">${b.createdAt.length() >= 10 ? b.createdAt.substring(0,10) : b.createdAt}</td>
                            <td class="views">${b.views}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>
</body>
</html>
