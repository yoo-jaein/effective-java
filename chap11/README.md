# Chapter 11 동시성

## 아이템 78. 공유 중인 가변 데이터는 동기화해 사용하라
> **핵심 정리**  
> 여러 스레드가 가변 데이터를 공유한다면 그 데이터를 읽고 쓰는 동작은 반드시 동기화해야 한다. 동기화하지 않으면 한 스레드가 수행한 변경을 다른 스레드가 보지 못할 수도 있다. 

### 동기화
멀티스레드 프로그램에서 여러 개의 스레드가 동일한 (가변) 리소스에 접근하려고 시도하고 결국 예측하지 못한 결과를 생성하는 문제가 발생할 수 있다. 이 문제를 해결하기 위해서는 특정 시점에 하나의 스레드만 리소스에 접근할 수 있도록 동기화해야 한다.  

동기화에는 비용이 들기 때문에 애초에 가변 데이터를 공유하지 않는 것이 가장 좋다. 가능한 한 불변 데이터만 공유하거나 아무것도 공유하지 말자. 가변 데이터는 단일 스레드에서만 쓰도록 하자.  

### synchronized
```java
private static synchronized void requestStop() {
	stopRequested = true;
}
private static synchronized boolean stopRequested() {
	return stopRequested; // 쓰기 메서드만 동기화해서는 충분하지 않다. 쓰기와 읽기 모두가 동기화되지 않으면 동작을 보장하지 않는다.
}
```
자바의 ```synchronized``` 키워드는 해당 메서드나 블록을 한번에 한 스레드씩 수행하도록 보장한다. 동기화된 블록에 들어가려는 다른 모든 스레드는 동기화된 블록 내부의 스레드가 블록을 나갈 때까지 접근할 수 없다.

### volatile
```java
private static volatile boolean stopRequested;
```
자바의 ```volatile``` 키워드는 해당 변수의 read와 write를 메인 메모리에서 수행하게 해준다. 필드를 ```volatile```로 선언하면 동기화를 생략해도 된다. 항상 가장 최근에 기록된 값을 읽게 됨을 보장해야 하는 경우 사용한다. 다만 CPU 캐시보다 메인 메모리에서의 비용이 더 크기 때문에 반드시 필요할 때만 ```volatile```을 사용해야 한다.

### 불변 객체
불변 객체를 만들려면 다음의 방법을 사용한다.

- ```setter``` 메서드를 제공하지 않는다. 
- 모든 필드를 ```final```이고 ```private```으로 만든다.
- 서브클래스가 메소드를 재정의하는 것을 허용하지 않는다. 
  - 클래스를 ```final```로 선언한다. 
  - 또는, 생성자를 ```private```으로 만들고 팩토리 메서드에서 인스턴스를 생성한다.
- 인스턴스 필드에 변경 가능한 개체에 대한 참조가 포함된 경우 해당 개체가 변경되지 않도록 한다.
  - 변경 가능한 객체를 수정하는 메소드를 제공하지 않는다.
  - 변경 가능한 객체에 대한 참조를 공유하지 않는다. 생성자에 전달된 변경 가능한 외부 개체에 대한 참조를 직접 저장하지 말자. 필요한 경우 복사본을 만들고 복사본에 대한 참조를 저장한다. 마찬가지로, 메서드에서 원본을 반환하지 않도록 필요한 경우 내부 변경 가능한 개체의 복사본을 만들고 이를 전달하자.

### 사실상 불변 객체

```사실상 불변(Effectively immutable)``` 객체란 변경할 수 있는 필드를 가지고 있지만 해당 필드에 대한 참조를 제공하지 않기 때문에 필드를 변경할 수 없는 객체를 말한다. 혹은, 기술적으로 변경될 수 있지만 특정 컨텍스트 내에서 변경되지 않는 것이 입증될 수 있는 경우에도 사실상 불변 객체에 해당한다. 필드의 값이나 객체 참조가 영원히 동일하게 유지되는 불변 객체와 미묘하게 차이가 있다. ```사실상 불변 객체```에 포함되는 개념인 ```고정된(Frozen) 객체```는 어떤 객체가 특정 지점까지 변경 가능하지만 객체를 고정하기로 결정하여 내부 상태에 대한 추가 변경을 더이상 허용하지 않는 객체를 말한다.

---

## 아이템 79. 과도한 동기화는 피하라
> **핵심 정리**  
> 데드락과 데이터 훼손을 피하려면 동기화 영역 안에서 외계인 메서드를 절대 호출하지 말자. 동기화 영역 안에서의 작업은 최소한으로 줄이자. 멀티코어 세상인 지금은 과도한 동기화를 피하는 게 어느 때보다 중요하다. 합당한 이유가 있을 때만 내부에서 동기화하고, 동기화했는지 여부를 문서에 명확히 밝히자.

과도한 동기화는 성능을 떨어뜨리고, 데드락(교착상태)에 빠뜨리고, 심지어 예측할 수 없는 동작을 낳기도 한다. 이런 상황을 피하려면 동기화 메서드나 블록 안에서는 제어를 절대로 클라이언트에 양도하면 안 된다. 다시 말하자면, 동기화된 영역 안에서의 작업은 최소한해야 하며 외부에서 들어온 객체의 메서드(외계인 메서드, alien method)를 함부로 호출해서는 안 된다. 외계인 메서드가 하는 일에 따라 동기화된 영역에서 문제가 발생할 수 있다.  

 멀티코어가 일반화된 오늘날, 과도한 동기화가 초래하는 진짜 비용은 락을 얻는 데 드는 CPU 시간이 아니다. 진짜 비용은 경쟁하느라 낭비하는 시간, 병렬로 실행될 기회를 잃고 모든 코어가 메모리를 일관되게 보기 위한 지연 시간이다.  

가변 클래스를 작성하려거든 다음 두 선택지 중 하나를 따르자. 선택하기 어렵다면 동기화하지 말고, 문서에 "스레드 안전하지 않다"고 명시하자.
1. 클래스 내부에서 동기화를 하지 말고, 그 클래스를 동시에 사용해야 하는 클라이언트에서 알아서 동기화하게 하자.
2. 클래스 내부에서 동기화를 서행해서 스레드 세이프한 클래스로 만들자. 단, 외부에서 락을 거는 것 보다 동시성을 월등히 개선할 수 있을 때만 선택한다.

자바도 초창기에는 위 지침을 따르지 않은 클래스가 많았다. StringBuffer의 인스턴스는 거의 항상 단일 스레드에서 쓰였음에도 내부적으로 동기화를 수행했다. 뒤늦게 StringBuilder(동기화하지 않은 StringBuffer)가 등장한 이유이기도 하다.

---

## 아이템 80. 스레드보다는 실행자, 태스크, 스트림을 애용하라

### 실행자 프레임워크
java.util.concurrent는 실행자 프레임워크(Executor framework)라고 하는 인터페이스 기반의 유연한 태스크 실행 기능을 담고 있다.

```java
ExecutorService exec = Executors.newSingleThreadExecutor(); // 작업 큐를 생성한다
exec.execute(runnable); // 실행자에 실행할 태스크를 넘긴다
exec.shutdown(); // 실행자를 종료시킨다
```
작업 큐는 클라이언트가 요청한 작업을 백그라운드 스레드에 위임해 비동기적으로 처리한다. 작업 큐가 필요 없어지면 클라이언트는 큐에 중단을 요청할 수 있고, 그러면 큐는 남아 있는 작업을 모두 완료한 후 스스로 종료한다.

### 실행자 서비스
실행자 서비스는 다음의 주요 기능을 제공한다.
- 특정 태스크가 완료되기를 기다린다. (get())
- 태스크 모음 중 아무것 하나(invokeAny()) 혹은 모든 태스크(invokeAll())가 완료되기를 기다린다.
- 실행자 서비스가 종료하기를 기다린다. (awaitTermination())
- 완료된 태스크들의 결과를 차례로 받는다. (ExecutorCompletionService)
- 태스크를 특정 시간에 혹은 주기적으로 실행하게 한다. (ScheduledThreadPoolExecutor)

작업 큐를 직접 만들거나 스레드를 직접 다루는 일은 일반적으로 삼가야 한다. 스레드를 직접 다루면 Thread가 작업 단위와 수행을 모두 수행하게 된다. 반면, 위의 실행자 프레임워크에서는 작업 단위(태스크)와 실행이 분리된다. 

### 태스크의 종류
태스크에는 두 가지가 있다. 
- Runnable
- Callable : Runnable과 비슷하지만 값을 반환하고 임의의 예외를 던질 수 있다.

### 포크-조인 태스크
자바 7이 되면서 실행자 프레임워크는 포크-조인(fork-join) 태스크를 지원하도록 확장되었다. ForkJoinTask의 인스턴스는 작은 하위 태스크로 나뉠 수 있고, ForkJoinPool을 구성하는 스레드들이 이 태스크를 처리한다. 일을 먼저 끝낸 스레드는 다른 스레드의 남은 태스크를 가져와 대신 처리할 수도 있다. 이렇게 하여 모든 스레드가 바쁘게 움직여 CPU를 최대한 활용하면서 높은 처리량과 낮은 지연시간을 달성한다.  

포크-조인 태스크를 직접 작성하고 튜닝하기란 어려운 일이지만, 포크-조인 풀을 이용해 만든 병렬 스트림(parallel stream)을 이용하면 적은 노력으로 그 이점을 얻을 수 있다.  

---

## 아이템 81. wait와 notify보다는 동시성 유틸리티를 애용하라
> **핵심 정리**  
> wait과 notify를 직접 사용하는 것을 어셈블리 언어로 프로그래밍하는 것에 비유할 수 있다. 반면 java.util.concurrent는 고수준 언어에 비유할 수 있다. 코드를 새로 작성한다면 wait과 notify를 직접 쓸 이유가 거의 없을 것이다.

wait과 notify는 올바르게 사용하기가 아주 까다로우니 고수준 동시성 유틸리티를 사용하자. 

### 고수준 동시성 유틸리티
java.util.concurrent의 고수준 유틸리티는 세 범주로 나눌 수 있다.
1. 실행자 프레임워크(Executor framework)
2. 동시성 컬렉션(concurrent collection)
3. 동기화 장치(synchronizer)

### 동시성 컬렉션
동시성 컬렉션은 List, Queue, Map 같은 표준 컬렉션 인터페이스에 동시성을 가미해 구현한 고성능 컬렉션이다. ConcurrentHashMap이 동시성 컬렉션에 해당한다. 동기화를 각자의 내부에서 수행하기 때문에, 동시성 컬렉션에서 동시성을 무력화하는 것은 불가능하며 외부에서 락을 추가로 사용하면 오히려 속도가 느려진다.  

### 동기화 장치
동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 하여 서로 작업을 조율할 수 있게 해준다. 가장 자주 쓰이는 CountDownLatch와 Semaphore다. 

#### CountDownLatch
```java
public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
	CountDownLatch ready = new CountDownLatch(concurrency);
	CountDownLatch start = new CountDownLatch(1);
	CountDownLatch done = new CountDownLatch(concurrency);
	
	for (int i = 0; i < concurrency; i++) {
		executor.execute(() -> {
			ready.countDown(); // 준비 완료
			try {
				start.await();
				action.run();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				done.countDown(); // 작업 종료
			}
		});
	}
	
	ready.await(); // 모든 작업자가 준비될 때까지 기다린다
	long startNanos = System.nanoTime(); // 시간을 잴 때는 System.concurrentTimeMillis보다 정밀한 System.nanoTime을 사용하자
	start.countDown(); // 작업자들을 깨운다
	done.await(); // 모든 작업자가 일을 끝낼 때까지 기다린다
	return System.nanoTime() - startNanos;
```

CountDownLatch는 하나 이상의 스레드가 또다른 하나 이상의 스레드 작업이 끝날 때까지 기다리게 한다. CountDownLatch의 유일한 생성자에서는 래치의 countDown() 메서드를 몇 번 호출해야 대기 중인 스레드를 깨우는지 결정하는 int 값을 받는다.  

위 time() 메서드에 넘겨진 실행자는 concurrency 매개변수로 지정한 동시성 수준만큼의 스레드를 생성할 수 있어야 한다. 그렇지 못하면 이 메서드는 결코 끝나지 않을 것이다. 이런 상태를 스레드 기아 교착상태(thread starvation deadlock)라 한다.

---

## 아이템 82. 스레드 안전성 수준을 문서화하라
> **핵심 정리**  
> 모든 클래스가 자신의 스레드 안전성 정보를 명확히 문서화해야 한다. 이는 자바독이나 스레드 안전성 애너테이션을 사용할 수 있다.

### 스레드 안전성 수준
멀티스레드 환경에서도 API를 안전하게 사용하려면 클래스가 지원하는 스레드 안전성 수준을 정확히 명시해야 한다. 다음 목록은 스레드 안전성이 높은 순으로 나열한 것이다.
- 불변(immutable)
- 무조건적 스레드 안전(unconditionally thread-safe)
- 조건부 스레드 안전(conditionally thread-safe)
- 스레드 안전하지 않음(not thread-safe)
- 스레드 적대적(thread-hostile)

#### 불변
- String, Long, BigInteger
불변 클래스의 인스턴스는 상수와 같아서 외부에서 동기화할 필요도 없다.

#### 무조건적 스레드 안전
- AtomicLong, ConcurrentHashMap
무조건적 스레드 안전한 클래스의 인스턴스는 수정될 수 있으나, 내부에서 동기화하기 때문에 별도의 외부 동기화 없이 동시에 사용해도 안전한다. 

#### 조건부 스레드 안전
- Collections.synchronized 래퍼 메서드가 반환한 컬렉션
무조건적 스레드 안전과 같으나, 일부 메서드는 동시에 사용하려면 외부 동기화가 필요하다. Collections.synchronized가 반환한 컬렉션의 반복자는 외부에서 동기화해야 한다.

#### 스레드 안전하지 않음
- ArrayList, HashMap 등 기본 컬렉션
스레드 안전하지 않은 클래스의 인스턴스는 수정될 수 있다. 동시에 사용하려면 일련의 메서드 호출을 외부 동기화해야 한다.

#### 스레드 적대적
스레드 적대적인 클래스는 모든 메서드 호출을 외부 동기화로 감싸더라도 멀티스레드 환경에서 안전하지 않다. 이 수준의 클래스는 일반적으로 정적 데이터를 아무 동기화 없이 수정한다. 동시성을 고려하지 않고 작성하다 보면 우연히 만들어질 수 있다.

---

## 아이템 83. 지연 초기화는 신중히 사용하라
> **핵심 정리**  
> 대부분의 필드는 지연시키지 말고 곧바로 초기화해야 한다. 성능이나 초기화 순환을 막기 위해 꼭 지연 초기화를 써야 한다면 올바른 기법을 사용하자. 

### 지연 초기화
지연 초기화(lazy initialization)는 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법이다. 값이 전혀 쓰이지 않으면 초기화가 일어나지 않는다. 지연 초기화는 주로 최적화 용도로 쓰이지만, 클래스와 인스턴스를 초기화할 때 발생할 수 있는 순환 문제를 해결하는 효과도 있다.  

그러나 지연 초기화는 양날의 검이다. 꼭 필요할 때까지는 하지 말자. 대부분의 상황에서는 일반적인 초기화가 지연 초기화보다 낫다.

### 순환 문제
```java
public class A { 
	private B b;
	
	public A() {
		this.b = new B();
	}
}
```

```java
public class B { 
	private A a;
	
	public B() {
		this.a = new A();
	}
}
```
순환 종속성 문제(Circular dependency problem)는 서로 직/간접적으로 의존하는 둘 이상의 모듈 사이의 관계를 말한다. 예를 들어, 클래스 A의 생성자에서 클래스 B의 인스턴스를 생성하고, 클래스 B의 생성자에서 클래스 A의 인스턴스를 생성하는 경우 순환 종속성 문제에 해당한다.

```java
A object = new A();
```
클래스 A의 생성자는 클래스 B의 생성자를 호출하고, 클래스 B의 생성자는 클래스 A의 생성자를 호출하기 때문에 무한 재귀 호출에 빠진다. 따라서 위 코드를 실행하면 StackOverflowError가 발생한다.

---

## 아이템 84. 프로그램의 동작을 스레드 스케줄러에 기대지 말라
> **핵심 정리**  
> 프로그램의 동작을 스레드 스케줄러에 기대지 말자. 프로그램의 견고성과 이식성을 모두 해치는 행위다.

여러 스레드가 실행 중이면 운영체제의 스레드 스케줄러가 어떤 스레드를 얼마나 오래 실행할지 정한다. 이 때 구체적인 스케줄링 정책은 운영체제마다 다를 수 있다. 따라서 정확성이나 성능이 스레드 스케줄러에 따라 달라지는 프로그램이라면 다른 플랫폼에 이식하기 어렵다.  

견고하고 이식성 좋은 프로그램을 작성하는 가장 좋은 방법은 실행 가능한 스레드의 평균적인 수를 프로세서 수보다 지나치게 많아지지 않도록 하는 것이다. 그래야 스레드 스케줄러가 고민할 거리가 줄어든다. 실행 준비가 된 스레드들은 맡은 작업을 완료할 때까지 계속 실행되도록 만들어야 한다.

## 참고
https://docs.oracle.com/javase/tutorial/essential/concurrency/imstrat.html  
http://tutorials.jenkov.com/java-concurrency/volatile.html  
https://self-learning-java-tutorial.blogspot.com/2016/10/how-to-resolve-circular-dependency.html  