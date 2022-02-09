package chap07;

public class HelloWorld {
	public static void main(String[] args) {
		"Hello world!".chars().forEach(System.out::print);
		System.out.println();
		"Hello world!".chars().forEach(x -> System.out.print((char) x));
	}
}
