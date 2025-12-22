plugins {
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	kotlin("plugin.jpa") version "1.9.22"
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	// Web機能（APIを作るために必要）
	implementation("org.springframework.boot:spring-boot-starter-web")
	// データ検証（入力チェック用）
	implementation("org.springframework.boot:spring-boot-starter-validation")
	// データベース連携（JPA）
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// JSON処理（KotlinでJSONを扱いやすくする）
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// 簡易データベース H2（開発用。インストール不要ですぐ動く）
	runtimeOnly("com.h2database:h2")

	// テスト用ツール
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	// Kotlin用の強力なモックライブラリ（テストコードで使用）
	testImplementation("io.mockk:mockk:1.13.9")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}