# talentflow-hr - 人力资源管理平台 | Human Resource Management Platform

[![Build](https://github.com/however-yir/talentflow-hr/actions/workflows/talentflow-smoke.yml/badge.svg)](https://github.com/however-yir/talentflow-hr/actions/workflows/talentflow-smoke.yml)
[![Docs](https://img.shields.io/badge/docs-README-0A7EFA)](https://github.com/however-yir/talentflow-hr#readme)
[![License](https://img.shields.io/badge/license-pending%20verification-EAB308)](./LICENSE_STATUS.md)
[![Status](https://img.shields.io/badge/status-showcase--ready-2563EB)](https://github.com/however-yir/talentflow-hr)
[![Series](https://img.shields.io/badge/series-java%20full--stack-7C3AED)](https://github.com/however-yir/however-yir#project-map)

> Status: `showcase-ready`
>
> Upstream origin: `lenve/vhr`
>
> License note: upstream repository currently has no detected GitHub license file, so public redistribution should be verified before shipping a formal `LICENSE`.

🔥 面向人事业务数字化的 Spring Boot + Vue 项目，覆盖组织、审批、报表与后台管理。  
🚀 当前重点是把传统二开仓库升级成“更适合作品集展示与后续工程化迭代”的独立项目。  
⭐ 适合放在 Java 全栈产品化作品线里，与 `nebulacms`、`aurora-mall` 一起看。

## 项目快照

- 定位：Java 全栈人力资源管理平台。
- 亮点：组织与流程场景、前后端分离、数据库初始化资源、后续可继续做 CI 与截图展示。
- 最短运行路径：`cd talentflow-platform && mvn -B -DskipTests package`
- 合规提醒：在补正式 `LICENSE` 之前，请先以 `LICENSE_STATUS.md`、`NOTICE.md` 与上游仓库状态为准。

## Java 全栈作品线分工

| Repo | 主要角色 | 技术侧重 | 最适合的展示点 |
| --- | --- | --- | --- |
| `NebulaCMS` | 内容平台 | 插件系统、WebFlux、Vue 3 | 插件生态、内容管理、平台化 |
| `TalentFlow HR` | 业务后台 | Spring Boot + Vue | 组织流程、人事场景、后台系统 |
| `Aurora Mall` | 电商系统 | Spring Boot + MyBatis | 商品交易、配置治理、质量门禁 |

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 目标与场景](#2-目标与场景)
- [3. 核心能力](#3-核心能力)
- [4. 技术栈](#4-技术栈)
- [5. 仓库结构](#5-仓库结构)
- [6. Quick Start](#6-quick-start)
- [7. 配置建议](#7-配置建议)
- [8. 开发与测试](#8-开发与测试)
- [9. 协作与发布](#9-协作与发布)
- [10. 路线图](#10-路线图)
- [11. 贡献指南](#11-贡献指南)
- [12. License](#12-license)

## 1. 项目概述

本仓库以工程化可维护为目标，强调文档清晰、结构稳定、可持续迭代。

## 2. 目标与场景

适用场景：

- 作为业务功能开发与验证的基础仓库。
- 作为团队内部协作与知识沉淀的载体。
- 作为后续扩展和二次开发的起点。

## 3. 核心能力

- 支持组织人事基础数据管理。
- 支持流程审批与业务协同。
- 支持统计报表与运营分析。

## 4. 技术栈

- Node.js / JavaScript
- Java / Spring
- Docker Compose

## 5. 仓库结构

建议优先阅读：

- README.md：项目入口与整体说明。
- docs 或同类目录：架构、规范、部署与 FAQ。
- 核心源码目录：按模块深入阅读。

## 6. Quick Start

1. 克隆仓库并进入目录：

```bash
git clone https://github.com/however-yir/talentflow-hr.git
cd talentflow-hr
```

2. 安装依赖并启动（按项目类型选择）：

```bash
# Start infra dependencies
docker compose up -d

# Initialize database schema
mysql -h 127.0.0.1 -uroot -proot-password talentflow_hr < talentflow_hr.sql

# Backend smoke path
cd talentflow-platform
mvn -B -DskipTests package
mvn -pl talentflow-server/talentflow-web spring-boot:run

# Frontend smoke path
cd ../talentflow-ui
npm ci
npm run serve
```

完成以上步骤后，默认可分别在 `8081` 和 Vue 开发端口观察后端与前端本地联调结果。

3. 最小验证建议：

- 依赖安装成功。
- 核心流程可运行。
- 基础测试或检查通过。

## 7. 配置建议

建议按 dev / staging / prod 分层配置，并将密钥类信息放入环境变量或密钥管理系统。

## 8. 开发与测试

推荐流程：

1. 基于默认分支创建功能分支。
2. 小步提交并保持提交目标单一。
3. 本地完成构建与测试后再推送。
4. 通过 Pull Request 完成评审与合并。

## 9. 协作与发布

建议使用语义化版本，发布说明应包含新增、修复与兼容性说明。

## 10. 路线图

建议按以下顺序推进：

1. 稳定主流程与关键接口。
2. 优化模块边界与可观测性。
3. 完善自动化测试与文档体系。

## 11. 贡献指南

提交建议包含：变更背景、实现说明、验证结果、风险评估。

## 12. License

请以仓库内现有 License 文件为准。
