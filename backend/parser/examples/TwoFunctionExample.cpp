int main(int argc, char** argv)
{
	// if encapsulaed with brackets its picked as one expression statement because expression statement need to end with a semi-colon.
	n1::n2::n3.c1.c2.c3->heaven(hello(), three.js());
	n1::n2::n3.T1<int>.T2<std::string>f1(math::PI).c1.c2.c3->heaven(hello(), three.js());

	heaven();

	std::sort(whatever(), someList);


	A::foo<int> ( arr );


    return 0;
}

// Function pointers are not parsed in current implementation.

bool heaven() {
    return true;
}