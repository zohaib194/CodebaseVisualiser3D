#include <iostream>

using namespace std;
class helloworld
{
public:
	helloworld();
	print();
	~helloworld();
	
};

int main()
{
    std::cout << "Hello World!";
    return 0;
}

namespace hello{
	void foo();

	void boo(){

	}
}

void hello::foo(){

}

void helloworld::print(){

}