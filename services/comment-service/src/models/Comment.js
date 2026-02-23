const { ObjectId } = require('mongodb');

/**
 * 评论模型类
 * 负责评论数据的结构定义和数据验证
 */
class Comment {
    /**
     * 创建评论对象
     * @param {Object} data - 评论数据
     * @returns {Object} 格式化的评论对象
     */
    static create(data) {
        return {
            // 评论id - 如果提供则使用，否则自动生成
            _id: data._id ? new ObjectId(data._id) : new ObjectId(),
            // 资源id
            resourceId: data.resourceId,
            // 父评论id
            parentId: data.parentId || null,
            // 用户id
            userId: data.userId,
            // 头像
            avatar: data.avatar || '',
            // 用户名
            userName: data.userName,
            // 评论内容
            content: data.content,
            // 回复数
            replyCount: data.replyCount || 0,
            // 点赞数
            likeCount: data.likeCount || 0,
            // 点赞用户列表
            likedUsers: data.likedUsers || [],
            // 评论时间
            time: data.time || new Date(),
            // 更新时间
            updatedAt: new Date(),
            // 状态（0：隐藏，1：显示，2：删除）
            status: data.status !== undefined ? data.status : 1
        };
    }

    /**
     * 验证评论数据
     * @param {Object} data - 评论数据
     * @returns {Object} 验证结果 { isValid: boolean, errors: string[] }
     */
    static validate(data) {
        const errors = [];

        if (!data.resourceId) {
            errors.push('缺少资源ID');
        }

        if (!data.userId) {
            errors.push('缺少用户ID');
        }

        if (!data.userName) {
            errors.push('缺少用户名');
        }

        if (!data.content || data.content.trim().length === 0) {
            errors.push('评论内容不能为空');
        } else if (data.content.length > 1000) {
            errors.push('评论内容不能超过1000字符');
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * 评论状态枚举
     */
    static STATUS = {
        HIDDEN: 0,
        VISIBLE: 1,
        DELETED: 2
    };
}

module.exports = Comment;