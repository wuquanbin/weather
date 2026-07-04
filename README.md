# 佛山本地智能气象出行服务系统

基于 Spring Boot + Vue3 + 微信小程序的智能气象出行服务平台，为佛山五区居民提供实时天气查询、预警通知、出行建议等一站式气象出行服务。

## 项目架构

```
weather/
├── SpringBoot/SpringBoot/    # 后端服务 (Java 17 + Spring Boot 3)
├── Vue3/foshanweather/       # 后台管理系统 (Vue 3 + TypeScript)
├── weixin/                   # 微信小程序 (原生微信小程序)
└── scripts/                  # 辅助脚本
```

## 功能模块

### 微信小程序端

- **首页** - 实时天气展示、区域切换、未来天气预报、气象预警、出行建议
- **出行** - POI 搜索、路线规划（步行/骑行/驾车/公交）、出行推荐
- **预警** - 气象预警列表、按级别筛选、预警详情
- **我的** - 微信登录、收藏地点、意见反馈、预警级别设置、系统设置

### 后台管理系统

- **数据看板** - 用户统计、天气概览、天气预报、预警统计、反馈统计
- **预警管理** - 预警信息增删改查
- **用户管理** - 小程序用户管理
- **反馈管理** - 用户反馈处理
- **出行管理** - 出行景点/路线管理
- **天气管理** - 天气数据查看与管理
- **系统管理** - 系统参数配置

### 后端服务

- RESTful API 接口
- JWT 身份认证
- 定时天气数据同步（高德地图 API + Open-Meteo API）
- 气象预警数据管理
- 出行路线规划（高德地图 API）

## 技术栈

| 层级         | 技术                                                         |
| ------------ | ------------------------------------------------------------ |
| 后端         | Spring Boot 3, Spring Data JPA, Spring Security, MySQL       |
| 前端（后台） | Vue 3, TypeScript, Vite, Element Plus                        |
| 小程序       | 微信小程序原生框架                                           |
| 第三方 API   | 高德地图 API（天气/POI/路线）, Open-Meteo API（预报/体感温度） |
| 认证         | JWT (JSON Web Token)                                         |
| 构建         | Maven (后端), npm (前端)                                     |

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- 微信开发者工具
- 高德地图 API Key

### 1. 后端启动

```bash
cd SpringBoot/SpringBoot

# 配置数据库和API密钥
# 编辑 src/main/resources/application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/weather
# spring.datasource.username=root
# spring.datasource.password=your_password
# amap.api.key=your_amap_key

# 启动服务
./mvnw spring-boot:run
```

后端服务运行在 `http://localhost:8080`

### 2. 前端（后台管理）启动

```bash
cd Vue3/foshanweather

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端运行在 `http://localhost:5173`，默认管理员账号：`admin / admin123`

### 3. 微信小程序

1. 使用微信开发者工具打开 `weixin/` 目录
2. 在 `weixin/utils/request.js` 中配置后端 API 地址
3. 编译运行

## API 概览

| 接口                      | 方法 | 说明         |
| ------------------------- | ---- | ------------ |
| `/api/weather/current`    | GET  | 获取实时天气 |
| `/api/weather/forecast`   | GET  | 获取天气预报 |
| `/api/warnings/active`    | GET  | 获取活跃预警 |
| `/api/travel/suggestions` | GET  | 获取出行建议 |
| `/api/travel/route`       | GET  | 路线规划     |
| `/api/travel/poi`         | GET  | POI 搜索     |
| `/api/wechat/login`       | POST | 微信登录     |
| `/api/wechat/feedback`    | POST | 提交反馈     |
| `/api/admin/dashboard`    | GET  | 后台数据看板 |

## 数据来源

- **实时天气**：高德地图天气 API（佛山五区：禅城、南海、顺德、高明、三水）
- **天气预报**：Open-Meteo API（16天预报）
- **体感温度**：Open-Meteo API（综合温度、湿度、风速计算）
- **出行路线**：高德地图路线规划 API
- **POI 搜索**：高德地图 POI 搜索 API

## 覆盖区域

佛山市五区：

| 区域   | 代码      |
| ------ | --------- |
| 禅城区 | chancheng |
| 南海区 | nanhai    |
| 顺德区 | shunde    |
| 高明区 | gaoming   |
| 三水区 | sanshui   |
