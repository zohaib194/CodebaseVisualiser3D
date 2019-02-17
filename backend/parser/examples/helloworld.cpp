#include <iostream>

using namespace std;

class Helloworld
{	
	class inneclass
	{
	public:
		inneclass();
		void foo();
		~inneclass();
	private:
		bool lolFrominneClass();
	};
	void privateFuncWithOutPrivateSpec();
public:
	Helloworld();
	void publicFunc2();
	~Helloworld(){};
	bool publicFuncWithBody(){return true;}

	int i;
	int j;
	std::pair<std::string, std::string> pairTest;
private:
	void privateFunc1();
	
	int x;
	int y;
protected:
	bool protectedFunc1();
	bool protectedFunc2();
	bool protectedFunc3();


class helloworld
{
	class hei
	{
	public:
		hei();
		~hei();
		
	};
public:
	helloworld();
	void print();
	~helloworld();
	
};

void foo()
{
    class /* No name */ {
		public:
        float x;
        float y;
    } point;
    
    point.x = 42;
}

int main()
{
    std::cout << "Hello World!";
    return 0;
}

namespace hello{
	#include <iostream>

	void foo();

	void boo(){

	}
}

void hello::foo(){

}

void Helloworld::publicFunc2() {

}