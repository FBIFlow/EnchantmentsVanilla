package me.fbiflow.enchantmentsvanilla;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClassPerformer {

	private static final String SOURCE_ROOT = "src/main/java";
	private static final String OUTPUT_FILE = "project_analysis.txt";
	private static List<ClassInfo> allClasses = new ArrayList<>();

	private static boolean includeSourceCode = true;
	private static boolean showPackageStructure = true;
	private static boolean showClassDeclaration = true;
	private static boolean showInheritance = true;
	private static boolean showFields = true;
	private static boolean showConstructors = true;
	private static boolean showMethods = true;

	private static int totalLinesOfCode = 0;
	private static int totalClasses = 0;
	private static int totalMethods = 0;
	private static int totalFields = 0;

	public static void main(String[] args) {
		try {
			System.out.println("=== АНАЛИЗ СТРУКТУРЫ ПРОЕКТА ===");

			configureAnalysisSettings();

			System.out.println("\nРезультат будет записан в файл: " + OUTPUT_FILE);

			PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE));

			analyzeProjectStructure();

			System.out.println("\n=== СТАТИСТИКА ПРОЕКТА ===");
			System.out.println("Всего классов: " + totalClasses);
			System.out.println("Всего строк кода: " + totalLinesOfCode);
			System.out.println("Всего методов: " + totalMethods);
			System.out.println("Всего полей: " + totalFields);

			writeProjectInfoToFile(writer);

			writer.close();
			System.out.println("Анализ завершен. Результат сохранен в " + OUTPUT_FILE);

		} catch (Exception e) {
			System.err.println("Ошибка при анализе проекта: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void configureAnalysisSettings() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("\n=== НАСТРОЙКИ АНАЛИЗА ===");
		System.out.println("Введите настройки одной строкой в формате '0 1 1 0 1 0 1'");
		System.out.println("где 1 - включить, 0 - выключить");
		System.out.println();
		System.out.println("Порядок настроек:");
		System.out.println("1. Включить исходный код: " + (includeSourceCode ? "ДА" : "НЕТ"));
		System.out.println("2. Показать структуру пакетов: " + (showPackageStructure ? "ДА" : "НЕТ"));
		System.out.println("3. Показать объявления классов: " + (showClassDeclaration ? "ДА" : "НЕТ"));
		System.out.println("4. Показать наследование: " + (showInheritance ? "ДА" : "НЕТ"));
		System.out.println("5. Показать поля: " + (showFields ? "ДА" : "НЕТ"));
		System.out.println("6. Показать конструкторы: " + (showConstructors ? "ДА" : "НЕТ"));
		System.out.println("7. Показать методы: " + (showMethods ? "ДА" : "НЕТ"));
		System.out.println();
		System.out.print("Введите настройки: ");

		try {
			String input = scanner.nextLine().trim();
			String[] settings = input.split("\\s+");

			if (settings.length >= 7) {
				includeSourceCode = "1".equals(settings[0]);
				showPackageStructure = "1".equals(settings[1]);
				showClassDeclaration = "1".equals(settings[2]);
				showInheritance = "1".equals(settings[3]);
				showFields = "1".equals(settings[4]);
				showConstructors = "1".equals(settings[5]);
				showMethods = "1".equals(settings[6]);
			} else {
				System.out.println("Недостаточно параметров. Использую настройки по умолчанию.");
			}

		} catch (Exception e) {
			System.out.println("Ошибка при чтении настроек. Использую настройки по умолчанию.");
		}

		displayCurrentSettings();
	}

	private static void displayCurrentSettings() {
		System.out.println("\n=== ТЕКУЩИЕ НАСТРОЙКИ ===");
		System.out.println("✓ Включить исходный код: " + (includeSourceCode ? "ДА" : "НЕТ"));
		System.out.println("✓ Показать структуру пакетов: " + (showPackageStructure ? "ДА" : "НЕТ"));
		System.out.println("✓ Показать объявления классов: " + (showClassDeclaration ? "ДА" : "НЕТ"));
		System.out.println("✓ Показать наследование: " + (showInheritance ? "ДА" : "НЕТ"));
		System.out.println("✓ Показать поля: " + (showFields ? "ДА" : "НЕТ"));
		System.out.println("✓ Показать конструкторы: " + (showConstructors ? "ДА" : "НЕТ"));
		System.out.println("✓ Показать методы: " + (showMethods ? "ДА" : "НЕТ"));
		System.out.println("Начинаю анализ...\n");
	}

	public static void analyzeProjectStructure() throws Exception {
		File projectRoot = new File(SOURCE_ROOT);
		if (!projectRoot.exists()) {
			projectRoot = new File(".");
		}

		System.out.println("Поиск классов в: " + projectRoot.getAbsolutePath());
		findAndAnalyzeClasses(projectRoot, "");
	}

	private static void findAndAnalyzeClasses(File directory, String packageName) {
		File[] files = directory.listFiles();
		if (files == null) return;

		for (File file : files) {
			if (file.isDirectory()) {
				String newPackage = packageName.isEmpty() ?
								file.getName() : packageName + "." + file.getName();
				findAndAnalyzeClasses(file, newPackage);
			} else if (file.getName().endsWith(".java")) {
				analyzeSourceFile(file, packageName);
			}
		}
	}

	private static void analyzeSourceFile(File javaFile, String packageName) {
		try {
			String fileName = javaFile.getName();
			String className = fileName.substring(0, fileName.length() - 5);

			String fullClassName = packageName.isEmpty() ?
							className : packageName + "." + className;

			ClassInfo classInfo = new ClassInfo(fullClassName, packageName, className);

			List<String> lines = Files.readAllLines(javaFile.toPath());

			int fileLines = countNonEmptyLines(lines);
			classInfo.setLinesOfCode(fileLines);
			totalLinesOfCode += fileLines;
			totalClasses++;

			analyzeSourceCode(lines, classInfo);

			totalMethods += classInfo.getMethods().size();
			totalFields += classInfo.getFields().size();

			classInfo.setSourceCode(String.join("\n", lines));

			allClasses.add(classInfo);

		} catch (Exception e) {
			System.err.println("Ошибка анализа файла " + javaFile.getName() + ": " + e.getMessage());
		}
	}

	private static int countNonEmptyLines(List<String> lines) {
		int count = 0;
		for (String line : lines) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty() && !trimmed.startsWith("//") && !trimmed.startsWith("/*") && !trimmed.startsWith("*")) {
				count++;
			}
		}
		return count;
	}

	private static void analyzeSourceCode(List<String> lines, ClassInfo classInfo) {
		for (String line : lines) {
			String trimmedLine = line.trim();

			if (trimmedLine.isEmpty()) {
				continue;
			}

			if (trimmedLine.startsWith("public class") || trimmedLine.startsWith("class") ||
							trimmedLine.startsWith("public abstract class") || trimmedLine.startsWith("public interface")) {
				classInfo.setClassDeclaration(trimmedLine);

				if (trimmedLine.contains("extends")) {
					String afterExtends = trimmedLine.split("extends")[1].split("\\{")[0].trim();
					classInfo.setParentClass(afterExtends);
				}
				if (trimmedLine.contains("implements")) {
					String afterImplements = trimmedLine.split("implements")[1].split("\\{")[0].trim();
					classInfo.setInterfaces(afterImplements);
				}
			}

			if ((trimmedLine.startsWith("private") || trimmedLine.startsWith("public") ||
							trimmedLine.startsWith("protected")) &&
							!trimmedLine.contains("(") && trimmedLine.contains(";") &&
							!trimmedLine.contains(" class ")) {
				classInfo.addField(trimmedLine.replace(";", "").trim());
			}

			if ((trimmedLine.startsWith("public") || trimmedLine.startsWith("private") ||
							trimmedLine.startsWith("protected")) &&
							trimmedLine.contains("(") && trimmedLine.contains(")") &&
							!trimmedLine.contains(" class ") && !trimmedLine.contains(" interface ")) {

				if (trimmedLine.endsWith("{") || trimmedLine.endsWith(")") ||
								(trimmedLine.contains(")") && !trimmedLine.contains(";"))) {

					String methodDeclaration = trimmedLine.split("\\{")[0].trim();
					classInfo.addMethod(methodDeclaration);
				}
			}

			if (trimmedLine.contains(classInfo.getSimpleName() + "(")) {
				String constructorDecl = trimmedLine.split("\\{")[0].trim();
				if (!constructorDecl.contains("=") && !constructorDecl.contains("return")) {
					classInfo.addConstructor(constructorDecl);
				}
			}
		}
	}

	public static void writeProjectInfoToFile(PrintWriter writer) {
		writer.println("=== СТАТИСТИКА ПРОЕКТА ===");
		writer.println("Всего классов: " + allClasses.size());
		writer.println("Всего строк кода: " + totalLinesOfCode);
		writer.println("Всего методов: " + totalMethods);
		writer.println("Всего полей: " + totalFields);
		writer.println();

		writer.println("=== ДЕТАЛЬНАЯ ИНФОРМАЦИЯ О КЛАССАХ ===");
		writer.println("Найдено классов: " + allClasses.size());

		if (showPackageStructure) {
			writer.println("\n=== СТРУКТУРА ПАКЕТОВ ===");
			writePackageStructure(writer);
		}

		writer.println();

		for (ClassInfo classInfo : allClasses) {
			writeClassInfoToFile(writer, classInfo);
		}
	}

	private static void writePackageStructure(PrintWriter writer) {
		PackageNode root = buildCompletePackageStructure();

		writePackageNode(writer, root, "", true);
	}

	private static PackageNode buildCompletePackageStructure() {
		PackageNode root = new PackageNode("");

		for (ClassInfo classInfo : allClasses) {
			String[] packages = classInfo.getPackageName().split("\\.");
			PackageNode current = root;

			for (String pkg : packages) {
				if (!pkg.isEmpty()) {
					current = current.getOrCreateChild(pkg);
				}
			}
			current.addClass(classInfo.getSimpleName());
		}

		File projectRoot = new File(SOURCE_ROOT);
		if (!projectRoot.exists()) {
			projectRoot = new File(".");
		}
		scanFileSystemForPackages(projectRoot, "", root);

		return root;
	}

	private static void scanFileSystemForPackages(File directory, String currentPackage, PackageNode currentNode) {
		File[] files = directory.listFiles();
		if (files == null) return;

		for (File file : files) {
			if (file.isDirectory()) {
				String childPackage = currentPackage.isEmpty() ?
								file.getName() : currentPackage + "." + file.getName();

				PackageNode childNode = currentNode.getOrCreateChild(file.getName());

				scanFileSystemForPackages(file, childPackage, childNode);
			}
		}
	}

	private static void writePackageNode(PrintWriter writer, PackageNode node, String indent, boolean isLast) {
		if (!node.name.isEmpty()) {
			String nodeDisplayName = node.name;
				writer.println(indent + (isLast ? "└── " : "├── ") + nodeDisplayName + "/");
			indent += isLast ? "    " : "│   ";
		}

		List<PackageNode> allChildren = new ArrayList<>();
		allChildren.addAll(node.children);

		for (int i = 0; i < allChildren.size(); i++) {
			PackageNode child = allChildren.get(i);
			boolean lastChild = (i == allChildren.size() - 1) && node.classes.isEmpty();
			writePackageNode(writer, child, indent, lastChild);
		}

		for (int i = 0; i < node.classes.size(); i++) {
			String className = node.classes.get(i);
			boolean lastClass = (i == node.classes.size() - 1);
			writer.println(indent + (lastClass ? "└── " : "├── ") + className + ".java");
		}
	}

	private static void writeClassInfoToFile(PrintWriter writer, ClassInfo classInfo) {
		String[] split = classInfo.getFullName().split("\\.");
		if (split[split.length - 1].equals("ClassPerformer")) {
			return;
		}

		if (
		showClassDeclaration ||
		showInheritance ||
		showFields ||
		showConstructors ||
		showMethods){
			writer.println("\n" + "=".repeat(80));
			writer.println("КЛАСС: " + classInfo.getFullName());
		}

		if (showClassDeclaration && classInfo.getClassDeclaration() != null) {
			writer.println("Объявление: " + classInfo.getClassDeclaration());
		}

		if (showInheritance) {
			if (classInfo.getParentClass() != null) {
				writer.println("Наследуется от: " + classInfo.getParentClass());
			}

			if (classInfo.getInterfaces() != null) {
				writer.println("Реализует интерфейсы: " + classInfo.getInterfaces());
			}
		}

		if (showFields && !classInfo.getFields().isEmpty()) {
			writer.println("\n--- ПОЛЯ (" + classInfo.getFields().size() + ") ---");
			for (String field : classInfo.getFields()) {
				writer.println("  " + field);
			}
		}

		if (showConstructors && !classInfo.getConstructors().isEmpty()) {
			writer.println("\n--- КОНСТРУКТОРЫ (" + classInfo.getConstructors().size() + ") ---");
			for (String constructor : classInfo.getConstructors()) {
				writer.println("  " + constructor);
			}
		}

		if (showMethods && !classInfo.getMethods().isEmpty()) {
			writer.println("\n--- МЕТОДЫ (" + classInfo.getMethods().size() + ") ---");
			for (String method : classInfo.getMethods()) {
				writer.println("  " + method);
			}
		}

		if (includeSourceCode) {
			writer.println("\n--- ПОЛНЫЙ ИСХОДНЫЙ КОД ---");
			writer.println("```java");
			writer.println(classInfo.getSourceCode());
			writer.println("```");
		}

		if (
						showClassDeclaration ||
										showInheritance ||
										showFields ||
										showConstructors ||
										showMethods){
			writer.println("=".repeat(80));
		}

	}

	static class ClassInfo {
		private int linesOfCode;
		private String fullName;
		private String packageName;
		private String simpleName;
		private String classDeclaration;
		private String parentClass;
		private String interfaces;
		private String sourceCode;
		private List<String> fields = new ArrayList<>();
		private List<String> methods = new ArrayList<>();
		private List<String> constructors = new ArrayList<>();

		public ClassInfo(String fullName, String packageName, String simpleName) {
			this.fullName = fullName;
			this.packageName = packageName;
			this.simpleName = simpleName;
		}

		public int getLinesOfCode() { return linesOfCode; }
		public void setLinesOfCode(int linesOfCode) { this.linesOfCode = linesOfCode; }
		public String getFullName() { return fullName; }
		public String getPackageName() { return packageName; }
		public String getSimpleName() { return simpleName; }
		public String getClassDeclaration() { return classDeclaration; }
		public void setClassDeclaration(String classDeclaration) { this.classDeclaration = classDeclaration; }
		public String getParentClass() { return parentClass; }
		public void setParentClass(String parentClass) { this.parentClass = parentClass; }
		public String getInterfaces() { return interfaces; }
		public void setInterfaces(String interfaces) { this.interfaces = interfaces; }
		public List<String> getFields() { return fields; }
		public void addField(String field) { this.fields.add(field); }
		public List<String> getMethods() { return methods; }
		public void addMethod(String method) { this.methods.add(method); }
		public List<String> getConstructors() { return constructors; }
		public void addConstructor(String constructor) { this.constructors.add(constructor); }
		public String getSourceCode() { return sourceCode; }
		public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
	}

	static class PackageNode {
		String name;
		List<PackageNode> children = new ArrayList<>();
		List<String> classes = new ArrayList<>();

		public PackageNode(String name) {
			this.name = name;
		}

		public PackageNode getOrCreateChild(String packageName) {
			for (PackageNode child : children) {
				if (child.name.equals(packageName)) {
					return child;
				}
			}

			PackageNode newChild = new PackageNode(packageName);
			children.add(newChild);
			return newChild;
		}

		public void addClass(String className) {
			if (!classes.contains(className)) {
				classes.add(className);
			}
		}
	}
}