test: build_tests
	cd src && java -ea testing.Test
build_tests: src/testing/
	cd src && javac testing/test_cases/*.java && javac testing/Test.java