const express = require('express');
const router = express.Router();

/**
 * 评论路由模块
 * 定义所有评论相关的API路由
 * @param {CommentController} commentController - 评论控制器实例
 */
function setupCommentRoutes(commentController) {
    // 添加评论
    router.post('/', commentController.addComment.bind(commentController));
    
    // 获取评论详情
    router.get('/:commentId', commentController.getCommentById.bind(commentController));
    
    // 获取某资源的所有评论（支持分页）
    router.get('/resource/:resourceId', commentController.getCommentsByResource.bind(commentController));
    
    // 获取某用户的所有评论
    router.get('/user/:userId', commentController.getCommentsByUser.bind(commentController));
    
    // 获取某评论的所有回复
    router.get('/replies/:commentId', commentController.getReplies.bind(commentController));
    
    // 点赞/取消点赞评论
    router.post('/:commentId/like', commentController.toggleLike.bind(commentController));
    
    // 编辑评论
    router.put('/:commentId', commentController.updateComment.bind(commentController));
    
    // 删除评论（软删除）
    router.delete('/:commentId', commentController.deleteComment.bind(commentController));
    
    return router;
}

module.exports = setupCommentRoutes;