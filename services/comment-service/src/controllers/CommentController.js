const { ObjectId } = require('mongodb');
const Comment = require('../models/Comment');

/**
 * 评论控制器
 * 处理所有评论相关的业务逻辑
 */
class CommentController {
    constructor(db) {
        this.commentsCollection = db.collection('comments');
    }

    /**
     * 添加评论
     */
    async addComment(req, res) {
        try {
            const { parentId, resourceId, userId, content, userName, avatar } = req.body;
            
            // 验证评论数据
            const validation = Comment.validate(req.body);
            if (!validation.isValid) {
                return res.status(400).json({
                    code: 400,
                    message: validation.errors.join(', '),
                    data: null
                });
            }
            
            // 判断是否重复评论
            const isExist = await this.commentsCollection.findOne({
                resourceId,
                userId,
                parentId,
                content,
                status: { $ne: Comment.STATUS.DELETED } // 不考虑已删除的评论
            });
            
            if (isExist) {
                return res.status(409).json({
                    code: 409,
                    message: '重复评论',
                    data: null
                });
            }
            
            // 创建评论对象
            const commentData = Comment.create(req.body);
            
            // 根评论
            if (!parentId) {
                const result = await this.commentsCollection.insertOne(commentData);
                const newComment = await this.commentsCollection.findOne({ _id: result.insertedId });
                
                return res.status(201).json({
                    code: 200,
                    message: '评论成功',
                    data: newComment
                });
            } 
            // 子评论
            else {
                // 验证父评论ID格式
                if (!ObjectId.isValid(parentId)) {
                    return res.status(400).json({
                        code: 400,
                        message: '无效的父评论ID格式',
                        data: null
                    });
                }
                
                // 查询父评论是否存在
                const parentComment = await this.commentsCollection.findOne({ 
                    _id: new ObjectId(parentId),
                    status: { $ne: Comment.STATUS.DELETED } // 不考虑已删除的评论
                });
                
                if (!parentComment) {
                    return res.status(404).json({
                        code: 404,
                        message: '父评论不存在',
                        data: null
                    });
                }
                
                // 插入子评论
                const result = await this.commentsCollection.insertOne(commentData);
                
                // 更新父评论的回复数
                await this.commentsCollection.updateOne(
                    { _id: new ObjectId(parentId) },
                    { $inc: { replyCount: 1 } }
                );
                
                const newComment = await this.commentsCollection.findOne({ _id: result.insertedId });
                
                return res.status(201).json({
                    code: 200,
                    message: '评论成功',
                    data: newComment
                });
            }
        } catch (error) {
            console.error('添加评论失败:', error);
            return res.status(500).json({
                code: 500,
                message: '评论失败',
                data: null
            });
        }
    }

    /**
     * 获取某资源的所有评论（支持分页）
     */
    async getCommentsByResource(req, res) {
        try {
            const { resourceId } = req.params;
            console.log(resourceId);
            const page = parseInt(req.query.page) || 1;
            console.log(page);
            const limit = parseInt(req.query.limit) || 20;
            console.log(limit);
            const skip = (page - 1) * limit;
            console.log(skip);
            
            // 获取根评论（parentId为null或不存在）
            const comments = await this.commentsCollection
                .find({ 
                    resourceId, 
                    // status: Comment.STATUS.VISIBLE,
                    // $or: [{ parentId: null }, { parentId: { $exists: false } }]
                })
                .sort({ time: -1 }) // 按时间倒序
                .skip(skip)
                .limit(limit)
                .toArray();
            
            // 获取总数
            const total = await this.commentsCollection.countDocuments({
                resourceId,
                status: Comment.STATUS.VISIBLE,
                $or: [{ parentId: null }, { parentId: { $exists: false } }]
            });
            
            // 为每个评论获取前几条回复
            for (const comment of comments) {
                const commentIdStr = comment._id.toString();
                if (ObjectId.isValid(commentIdStr)) {
                    comment.recentReplies = await this.commentsCollection
                        .find({ parentId: commentIdStr, status: Comment.STATUS.VISIBLE })
                        .sort({ time: 1 })
                        .limit(3)
                        .toArray();
                } else {
                    comment.recentReplies = [];
                }
            }
            
            return res.status(200).json({
                code: 200,
                message: '获取数据成功',
                data: {
                    comments,
                    pagination: {
                        total,
                        page,
                        limit,
                        totalPages: Math.ceil(total / limit)
                    }
                }
            });
        } catch (error) {
            console.error('获取评论失败:', error);
            return res.status(500).json({
                code: 500,
                message: '获取数据失败',
                data: null
            });
        }
    }

    /**
     * 获取某用户的所有评论
     */
    async getCommentsByUser(req, res) {
        try {
            const { userId } = req.params;
            const page = parseInt(req.query.page) || 1;
            const limit = parseInt(req.query.limit) || 20;
            const skip = (page - 1) * limit;
            
            const comments = await this.commentsCollection
                .find({ userId, status: { $ne: Comment.STATUS.DELETED } })
                .sort({ time: -1 })
                .skip(skip)
                .limit(limit)
                .toArray();
            
            const total = await this.commentsCollection.countDocuments({
                userId,
                status: { $ne: Comment.STATUS.DELETED }
            });
            
            return res.status(200).json({
                code: 200,
                message: '获取数据成功',
                data: {
                    comments,
                    pagination: {
                        total,
                        page,
                        limit,
                        totalPages: Math.ceil(total / limit)
                    }
                }
            });
        } catch (error) {
            console.error('获取用户评论失败:', error);
            return res.status(500).json({
                code: 500,
                message: '获取数据失败',
                data: null
            });
        }
    }

    /**
     * 获取某评论的所有回复
     */
    async getReplies(req, res) {
        try {
            const { commentId } = req.params;
            const page = parseInt(req.query.page) || 1;
            const limit = parseInt(req.query.limit) || 20;
            const skip = (page - 1) * limit;
            
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return res.status(400).json({
                    code: 400,
                    message: '无效的评论ID格式',
                    data: null
                });
            }
            
            const replies = await this.commentsCollection
                .find({ parentId: commentId, status: Comment.STATUS.VISIBLE })
                .sort({ time: 1 })
                .skip(skip)
                .limit(limit)
                .toArray();
            
            const total = await this.commentsCollection.countDocuments({
                parentId: commentId,
                status: Comment.STATUS.VISIBLE
            });
            
            return res.status(200).json({
                code: 200,
                message: '获取回复成功',
                data: {
                    replies,
                    pagination: {
                        total,
                        page,
                        limit,
                        totalPages: Math.ceil(total / limit)
                    }
                }
            });
        } catch (error) {
            console.error('获取回复失败:', error);
            return res.status(500).json({
                code: 500,
                message: '获取数据失败',
                data: null
            });
        }
    }

    /**
     * 点赞/取消点赞评论
     */
    async toggleLike(req, res) {
        try {
            const { commentId } = req.params;
            const { userId } = req.body;
            
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return res.status(400).json({
                    code: 400,
                    message: '无效的评论ID格式',
                    data: null
                });
            }
            
            if (!userId) {
                return res.status(400).json({
                    code: 400,
                    message: '缺少用户ID',
                    data: null
                });
            }
            
            const comment = await this.commentsCollection.findOne({
                _id: new ObjectId(commentId),
                status: Comment.STATUS.VISIBLE
            });
            
            if (!comment) {
                return res.status(404).json({
                    code: 404,
                    message: '评论不存在',
                    data: null
                });
            }
            
            const likedUsers = comment.likedUsers || [];
            const isLiked = likedUsers.includes(userId);
            
            if (isLiked) {
                // 取消点赞
                await this.commentsCollection.updateOne(
                    { _id: new ObjectId(commentId) },
                    {
                        $pull: { likedUsers: userId },
                        $inc: { likeCount: -1 }
                    }
                );
                return res.status(200).json({
                    code: 200,
                    message: '取消点赞成功',
                    data: { isLiked: false }
                });
            } else {
                // 添加点赞
                await this.commentsCollection.updateOne(
                    { _id: new ObjectId(commentId) },
                    {
                        $push: { likedUsers: userId },
                        $inc: { likeCount: 1 }
                    }
                );
                return res.status(200).json({
                    code: 200,
                    message: '点赞成功',
                    data: { isLiked: true }
                });
            }
        } catch (error) {
            console.error('点赞操作失败:', error);
            return res.status(500).json({
                code: 500,
                message: '操作失败',
                data: null
            });
        }
    }

    /**
     * 编辑评论
     */
    async updateComment(req, res) {
        try {
            const { commentId } = req.params;
            const { content, userId } = req.body;
            
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return res.status(400).json({
                    code: 400,
                    message: '无效的评论ID格式',
                    data: null
                });
            }
            
            if (!content || !userId) {
                return res.status(400).json({
                    code: 400,
                    message: '缺少必要参数',
                    data: null
                });
            }
            
            const comment = await this.commentsCollection.findOne({
                _id: new ObjectId(commentId)
            });
            
            if (!comment) {
                return res.status(404).json({
                    code: 404,
                    message: '评论不存在',
                    data: null
                });
            }
            
            // 检查是否是评论作者
            if (comment.userId !== userId) {
                return res.status(403).json({
                    code: 403,
                    message: '无权编辑此评论',
                    data: null
                });
            }
            
            // 检查评论是否已删除
            if (comment.status === Comment.STATUS.DELETED) {
                return res.status(400).json({
                    code: 400,
                    message: '已删除的评论无法编辑',
                    data: null
                });
            }
            
            await this.commentsCollection.updateOne(
                { _id: new ObjectId(commentId) },
                {
                    $set: {
                        content,
                        updatedAt: new Date()
                    }
                }
            );
            
            const updatedComment = await this.commentsCollection.findOne({
                _id: new ObjectId(commentId)
            });
            
            return res.status(200).json({
                code: 200,
                message: '编辑成功',
                data: updatedComment
            });
        } catch (error) {
            console.error('编辑评论失败:', error);
            return res.status(500).json({
                code: 500,
                message: '编辑失败',
                data: null
            });
        }
    }

    /**
     * 删除评论（软删除）
     */
    async deleteComment(req, res) {
        try {
            const { commentId } = req.params;
            const { userId, isAdmin = false } = req.body;
            
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return res.status(400).json({
                    code: 400,
                    message: '无效的评论ID格式',
                    data: null
                });
            }
            
            if (!userId) {
                return res.status(400).json({
                    code: 400,
                    message: '缺少用户ID',
                    data: null
                });
            }
            
            const comment = await this.commentsCollection.findOne({
                _id: new ObjectId(commentId)
            });
            
            if (!comment) {
                return res.status(404).json({
                    code: 404,
                    message: '评论不存在',
                    data: null
                });
            }
            
            // 检查权限（作者或管理员）
            if (comment.userId !== userId && !isAdmin) {
                return res.status(403).json({
                    code: 403,
                    message: '无权删除此评论',
                    data: null
                });
            }
            
            // 软删除评论
            await this.commentsCollection.updateOne(
                { _id: new ObjectId(commentId) },
                {
                    $set: {
                        status: Comment.STATUS.DELETED, // 2表示已删除
                        updatedAt: new Date()
                    }
                }
            );
            
            // 如果是子评论，更新父评论的回复数
            if (comment.parentId && ObjectId.isValid(comment.parentId)) {
                const parentComment = await this.commentsCollection.findOne({
                    _id: new ObjectId(comment.parentId)
                });
                if (parentComment && parentComment.replyCount > 0) {
                    await this.commentsCollection.updateOne(
                        { _id: new ObjectId(comment.parentId) },
                        { $inc: { replyCount: -1 } }
                    );
                }
            }
            
            return res.status(200).json({
                code: 200,
                message: '删除成功',
                data: null
            });
        } catch (error) {
            console.error('删除评论失败:', error);
            return res.status(500).json({
                code: 500,
                message: '删除失败',
                data: null
            });
        }
    }

    /**
     * 获取评论详情
     */
    async getCommentById(req, res) {
        try {
            const { commentId } = req.params;
            
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return res.status(400).json({
                    code: 400,
                    message: '无效的评论ID格式',
                    data: null
                });
            }
            
            const comment = await this.commentsCollection.findOne({
                _id: new ObjectId(commentId),
                status: Comment.STATUS.VISIBLE
            });
            
            if (!comment) {
                return res.status(404).json({
                    code: 404,
                    message: '评论不存在',
                    data: null
                });
            }
            
            return res.status(200).json({
                code: 200,
                message: '获取成功',
                data: comment
            });
        } catch (error) {
            console.error('获取评论详情失败:', error);
            return res.status(500).json({
                code: 500,
                message: '获取失败',
                data: null
            });
        }
    }
}

module.exports = CommentController;