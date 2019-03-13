#include <iostream>

using namespace std;
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

int main()
{	
	lolasdasdasd();
    std::cout << "Hello World!";
    return 0;
}


int lol(){
	helloworld hw = new helloworld();
	hw.print();
	if(true){
		lol();
	}

	return 0;
}

namespace hello{
	#include <iostream>

	void foo();

	void boo(){
		foo();
		foo(1);
		foo1(1);
		f1oo1(1);
		f1o1o1(1);
	}
}

void hello::foo(){
	lol();
}

void helloworld::print() {

}

union UnionTest {
	int aShit;
	int bShit;
};