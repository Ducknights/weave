const express = require('express');
const { MongoClient } = require('mongodb');
const cors = require('cors');
const dbConfig = require('../config/dbConfig');
const CommentController = require('../controllers/CommentController');
const setupCommentRoutes = require('../routes/commentRoutes');

/**
 * 评论服务器类 - 采用OOP设计模式重构
 */
class CommentServer {
    constructor() {
        this.app = express();
        this.port = 3001;
        this.uri = dbConfig.uri;
        this.client = new MongoClient(this.uri);
        this.db = null;
        this.commentController = null;

        // 初始化中间件
        this.initializeMiddlewares();
    }

    /**
     * 初始化中间件
     */
    initializeMiddlewares() {
        this.app.use(express.json());
        this.app.use(cors());
    }

    /**
     * 连接数据库
     */
    async connectDB() {
        try {
            await this.client.connect();
            console.log('✅ 连接到 MongoDB');
            this.db = this.client.db(dbConfig.databaseName);
            return this.db;
        } catch (error) {
            console.error('❌ 数据库连接失败:', error);
            process.exit(1);
        }
    }

    /**
     * 初始化路由
     */
    initializeRoutes() {
        // 创建评论控制器实例
        this.commentController = new CommentController(this.db);

        // 注册评论路由
        this.app.use('/api/comments', setupCommentRoutes(this.commentController));

        // 健康检查路由
        this.app.get('/health', (req, res) => {
            res.status(200).json({
                status: 'healthy',
                message: '评论服务运行正常'
            });
        });
    }

    /**
     * 启动服务器
     */
    async start() {
        await this.connectDB();
        this.initializeRoutes();

        this.app.listen(this.port, () => {
            console.log(`🚀 评论服务器运行在 http://localhost:${this.port}`);
            console.log(`📚 API 文档:`);
            console.log(`   GET    /health                          - 健康检查`);
            console.log(`   POST   /api/comments                    - 添加评论`);
            console.log(`   GET    /api/comments/:commentId         - 获取评论详情`);
            console.log(`   GET    /api/comments/resource/:resourceId - 获取资源评论`);
            console.log(`   GET    /api/comments/user/:userId       - 获取用户评论`);
            console.log(`   GET    /api/comments/replies/:commentId - 获取评论回复`);
            console.log(`   POST   /api/comments/:commentId/like    - 点赞/取消点赞`);
            console.log(`   PUT    /api/comments/:commentId         - 编辑评论`);
            console.log(`   DELETE /api/comments/:commentId         - 删除评论`);
        });
    }

    /**
     * 关闭服务器
     */
    async close() {
        try {
            await this.client.close();
            console.log('✅ 数据库连接已关闭');
        } catch (error) {
            console.error('❌ 关闭数据库连接失败:', error);
        }
    }
}

module.exports = CommentServer;

// 如果直接运行此文件，则启动服务器
if (require.main === module) {
    const server = new CommentServer();
    server.start();
}