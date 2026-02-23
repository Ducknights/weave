// 数据库配置文件
module.exports = {
    uri: 'mongodb://localhost:27017',
    databaseName: 'school',
    collectionNames: {
        comments: 'comments'
    },
    // 连接选项
    options: {
        useNewUrlParser: true,
        useUnifiedTopology: true
    }
};