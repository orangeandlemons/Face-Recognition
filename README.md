# 人脸识别签到APP

## 项目描述

这是一款基于人脸识别技术的考勤APP，主要用于课堂签到，旨在提高教师和学生的考勤管理效率和体验。

## 项目目标

开发一款功能全面、使用便捷的考勤APP，支持多种签到方式，确保签到的准确性和便捷性，优化课堂管理流程。

## 项目内容

- **需求分析、设计、开发和测试**: 独立完成APP的需求分析、设计、开发和测试工作。
- **功能实现**: 实现教师创建课程和学生通过课程码加入课程的功能。
- **地图定位**: 集成百度地图API，实现地图定位和距离检测功能。
- **人脸识别**: 使用face recognition库实现1:1身份验证类人脸识别功能。
- **手势识别**: 实现手势识别签到功能。
- **用户界面设计**: 设计并开发用户界面，确保良好的用户体验。
- **后端服务器配置**: 配置后端服务器，进行数据存储和管理。

## 项目成果

成功开发了一款功能全面的考勤APP，支持多种签到方式，提高了课堂考勤的效率和准确性。实现了高精度的人脸识别功能，确保了签到过程的安全和可靠。集成了百度地图API，实现了精确的定位和距离检测。

## 签到模块

签到模块功能包括：人脸识别签到、定位签到、手势签到。

- **人脸识别签到**: 通过采集学生图像，上传到服务器，进行人脸识别比对，获取签到结果。
- **定位签到**: 定位学生位置，上传位置信息，通过判断是否在签到范围内决定签到结果。
- **手势签到**: 学生输入签到手势，手势正确则签到成功。

## 介绍

人脸识别签到APP端代码

## 软件架构

### 后端

- **activity**
- **adapter**（适配器）
- **entity**（实体）
- **fragment**
- **popup**（标题框）
- **viewModel**

### 前端

- **layout**（界面代码）

## 使用说明

Android开发本身就属于前后端分离，前端为layout布局的设计，后端对布局对应的控件进行抓取，开发功能。安卓作为市场主流的移动端操作系统，应用前景广阔。系统APP端采用视图模型（Model-View-ViewModel, MVVM）架构，这是传统视图控制模型（MVC）架构的升级版。



# Face Recognition Attendance App

## Project Description

This is an attendance app based on facial recognition technology, primarily used for classroom attendance, aiming to enhance the efficiency and experience of attendance management for teachers and students.

## Project Objective

To develop a comprehensive and user-friendly attendance app that supports multiple check-in methods, ensuring accuracy and convenience in the check-in process, and optimizing classroom management.

## Project Content

- **Requirements Analysis, Design, Development, and Testing**: Independently completed the analysis, design, development, and testing of the app.
- **Functionality Implementation**: Enabled teachers to create courses and students to join courses using a course code.
- **Map Integration**: Integrated Baidu Maps API for location and distance detection.
- **Facial Recognition**: Utilized the face recognition library to achieve 1:1 identity verification.
- **Gesture Recognition**: Implemented gesture recognition check-in functionality.
- **User Interface Design**: Designed and developed the user interface to ensure a good user experience.
- **Backend Server Configuration**: Configured the backend server for data storage and management.

## Project Outcomes

Successfully developed a comprehensive attendance app supporting multiple check-in methods, improving the efficiency and accuracy of classroom attendance. Achieved high-precision facial recognition to ensure the security and reliability of the check-in process. Integrated Baidu Maps API to provide precise location and distance detection.

## Check-in Module

The check-in module includes the following features: facial recognition check-in, location check-in, and gesture check-in.

- **Facial Recognition Check-in**: Captures student images and uploads them to the server for facial recognition comparison to obtain the check-in result.
- **Location Check-in**: Tracks the student's location and uploads the information to determine the check-in result based on whether the location is within the check-in range.
- **Gesture Check-in**: Students input a check-in gesture, and if the gesture is correct, the check-in is successful.

## Introduction

Facial Recognition Attendance App client-side code

## Software Architecture

### Backend

- **activity**
- **adapter**
- **entity**
- **fragment**
- **popup**
- **viewModel**

### Frontend

- **layout** (interface code)

## Instructions

Android development inherently separates the frontend and backend, with the frontend focused on layout design and the backend handling the corresponding controls for functionality development. As Android is the mainstream mobile operating system in the market, it has a broad application prospect. The app client-side adopts the Model-View-ViewModel (MVVM) architecture, which is an upgrade from the traditional Model-View-Controller (MVC) architecture.
