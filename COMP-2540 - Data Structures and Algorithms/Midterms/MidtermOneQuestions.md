COMP-2540: MDTEMM EXAM 1 (15%) - All 60 Questions

Questions 1-5 are related to our Assignment One. Suppose that there are n tokens and the vocabulary size is m.

Q1. What is the task of assignment 1?
(A) Compare sorting algorithms     
(B) Reverse strings
(C) Three-way disjointness
(D) Count word frequency               Correct
(E) Element uniqueness problem

Q2. What is the time complexity for the program count_LINKED_LIST_BAD?
(A) O(n)
(B) O(nlog n)
(C) O(nm)
(D) O(nmm)               Correct
(E) O(nnm)      

Q3. What should be the time complexity for count_LINKED_LIST_GOOD?
(A) O(n)
(B) O(nlog n)
(C) O(nm)               Correct      
(D) O(nmm)
(E) O(nnm)

Q4. What is the time complexity for count_ARRAY?
(A) O(n)      
(B) O(nlog n)
(C) O(nm)               Correct
(D) O(nmm)
(E) O(nnm)

Q5. What is the time complexity if you sort the array first, then count the frequency?
(A) O(n)
(B) O(nlogn)               Correct      
(C) O(nm)
(D) O(nmm)
(E) O(nnm)

The next four questions are based on the following code 
long x(int n) {
   If (n <= 1)    return n;
   return x(n-2) + x(n-1);
}

Q6. What is the recurrence relation for the running time of the program?
(a) T(n) = T(n-1) + 1
(b) T(n) = T(n-1) + n
(c) T(n) = 2T(n/2) + 1
(d) T(n) = 2T(n/2) + n
(e) T(n) = T(n - 1) + T(n - 2) + 1                Correct      

Q7. The time complexity is
(a) Linear 
(b) Quadratic
(c) Exponential               Correct      
(d) Logarithmic

Q8. The best algorithm for the same problem can have what kind of time complexity?
(a) Linear               Correct
(b) Quadratic      
(c) Exponential
(d) Logarithmic

Q9. It defines which function?
(a) Factorial
(b) Fibonacci               Correct      
(c) Summation
(d) Max
(e) Min

Q10. The following defines which function?
public static int f(int[] arr, int n) {
    If (n == 1) return arr[0];
    Int m = f(arr, n-1);
    if (arr[n - 1] > m) return arr[n-1];
    return m; 
}
(a) Factorial
(b) Fibonacci
(c) Summation
(d) Max                Correct      
(e) Min

The next 3 questions are based on the following code:
Int x(int[] data, int low, int high) {
    if (low > high)   return 0;
    If (low == high)   return data[low];
    Int m = (low + high) / 2;
    Return x(data, low, m) + x(data, m+1, high)
}

Q11. The function of the code is to
(a) search for an element in an array      
(b) find the minimal element
(c) summation of the array               Correct
(d) sort an array

Q12. It's time complexity is
(a) O(1)
(b) O(n)               Correct
(c) O(n^2)
(d) O(logn)      
(e) O(nlogn)
(f) O(2^n)

Q13. It's recurrence relation for the running time is
(a) T(n) = T(n-1) + 1
(b) T(n) = T(n-1) + n
(c) T(n) = 2T(n/2) + 1               Correct      
(d) T(n) = 2T(n/2) + n
(e) T(n) = T(n - 1) + T(n - 2) + 1

Q14. Consider an array of size 10 (indices are from 0 to 9). The recursive algorithm for reversing an array studied in class, namely reverse Array(A, i, j), makes the following recursive calls (in that order):

void reverseArray(int [] data, int low, int high) {
     If (low < high) {
         int temp = data [low];
        data [low] = data [high];
        data [high] = temp;
        reverseArray(data, low + 1, high - 1);
       }
}

(a) reverseArray(A, 4, 6), reverseArray(A, 3, 7), reverseArray(A, 2, 8), etc.
(b) reverse Array(A, 0, 9), reverseArray(A, 1, 8), reverseArray(A, 2, 7), etc.               Correct      
(c) reverseArray(A, 0, 1), reverseArray(A, 0, 2), reverseArray(A, 0, 3), etc.
(d) reverseArray(A, 9, 8), reverseArray(A, 7, 6), reverseArray(A, 5, 4), etc.


Q15. The following sequence is equal to
1 + 2 + 3 + … + n =

(a) 2n
(b) n^2
(c) n^2 / 2
(d) n^2 / 2 + n/2               Correct      
(e) n(n-1) / 2

Q16. Which is the closest to the following sequence
1/1 + 1/2 + 1/3 … 1/n

(a) 2n
(b) nlogn
(c) logn
(d) In n               Correct      
(e) n

Q17. The following sequence is equal to
1 + 2^1 + 2^2 + … + 2^(n-1)

(a) 2^n      
(b) n^2
(c) n^2 - 1
(d) n^2 + n
(e) 2^n - 1               Correct

Q18. Given the following algorithm to find the max element from an array.

int arrayMax(int [] data) {
   int n = data.length;
   int currentMax=data[0];
   for (int j = 1; j < n ; j++) 
           If (data [j] > currentMax)
                  currentMax = data[j];
   return currentMax;
}
How many times the assignment statement currentMax = data[j] is executed on average

(a) n
(b) n/2
(c) ln n                Correct      
(d) nlogn
(e) logn

Q19. The following program calculates the prefix average as we discussed in class. Namely, given a sequence x consisting of n numbers, we want to compute a sequence a such that aj is the average of elements x0, ... , xj, for j = 0, …, n - 1, that is,

aj = (Sigma i = 0 to j xi) / j + 1

double[ ] prefixAverage (double [ ] x) {
   int n = x.length;
   double[] a = new double [n] ;
   for (int j=0; j < n; j++) {
      double total = 0;
      for (int i=0; i <= j; i++)
          total += x[i];
      a[j] = total / (j+1);
   }
   return a;
}

The code can be improved so that the better code has time complexity
(a) O(1)
(b) O(n)               Correct
(c) O(n^2)      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q20. Which of the following is a disadvantage of using arrays over linked lists?
(A) Arrays require contiguous memory allocation
(B) The size of an array is fixed after declaration
(C) Insertion and deletion at arbitrary positions are slow
(D) All of the above               Correct      

Q21. Which of the following is NOT true about a singly linked list?
(a) The elements are stored in non-contiguous memory locations
(B) Each node has a pointer to the next node in the sequence.
(C) Insertion at the head takes O 1) time.
(D) Accessing the n-th element takes O(1) time. Correct 

Q22. What is the time complexity of searching for an element in an unsorted array of size n?
(a) O(1)
(b) O(n)               Correct
(c) O(n^2)      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q23. What is the time complexity of searching for an element in a sorted array of size n using binary search?
(a) O(1)
(b) O(n)
(c) O(n^2)
(d) O(logn)               Correct
(e) O(nlogn)      
(f) O(2^n)

Q24. What is the worst-case time complexity of deleting the tail node in a singly linked list with n nodes?
(a) O(1)
(b) O(n)               Correct      
(c) O(n^2)
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)



Q25. What is the time complexity of the following program:
public static String boolean x(int [] data) {
    int n = data.length;
    Int[] temp = Arrays.copyOf(data,n);
    Arrays.sort(temp);
    for (int j = 0; j < n-1; j++)
       if (temp[j] == temp[j+1])
           Return false;
    Return true;
}
(a) O(1)
(b) O(n)
(c) O(n^2)      
(d) O(logn)
(e) O(nlogn)               Correct
(f) O(2^n)

Q26. What is the time complexity for the following code:
boolean x(int[] groupA,int[] groupB,int[] groupC){
    for (int a : groupA)
         For (int b : groupB)
               If (a == b)
                  for (int c : group C)
                       If (a == c)
                          Return false;
    Return true;
}
(a) O(1)
(b) O(n)
(c) O(n^2)               Correct      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q27. What is the time complexity for the following program:
public static String aFunction(char c, int n) {
     String answer = “ “;
     for (int j=0; j<n; j++)
         answer += c;
   return answer;
}
(a) O(1)
(b) O(n)
(c) O(n^2)               Correct      
(d) O(log n)
(e) O(nlogn)
(f) O(2^n)


Q28. What is the time complexity of the following program:
public static String aFunction(int c, int n) {
     Int answer = 0;
     for (int j = 0; j < n; j++)
          Answer += c;
     Return answer;
}
(a) O(1)
(b) O(n)               Correct      
(c) O(n^2)
(d) O(log n)
(e) O(nlogn)
(f) O(2^n)

Q29. Given the following merge sort code, which line has a logic error? The Java syntax is correct.
<K>void mergeSort(K[] S, Comparator<K> comp) {
     Int n = S.length;
     if (n <= 2) return;
     Int mid = n/2;
     K[] S1 = Arrays.copyOfRange(S, 0, mid);
     K[] S2 = Arrays.copyOfRange(S, mid, n);
     mergeSort(S1, comp);
     mergeSort(S2, comp);
     merge(S1, S2, S, comp);
}
(a) 3               Correct      
(b) 4
(c) 5
(d) 6
(e) 7

Q30. Which line of the following code make the code non-terminating?
Int x(int n) {
    If (n==0)
       Return 1;
    Else
       Return n * x(n)
}
(a) 1
(b) 2
(c) 3
(d) 4
(e) 5               Correct      
Q31Which of the following operations on an array has a time complexity of O (1) in the worst case?
(A) Searching for an element.
(B) Deleting an element.
(C) Inserting an element at a given index.
(D) Accessing an element at a specific index.Correct

Q32. Which of the following is true for doubly linked lists but not for singly linked lists?
(A) They allow traversal in both forward and backward directions.               Correct      
(B) Each node has a pointer to the next node.
(C) Insertion at the head is O(1).
(D) Deletion of the head node takes O(1) time.

Q33. Given functions f(n) = 3n^2 - 2n + 2 logn and g(n) = 2n^2 + 10n + 2nlogn, which of the following is correct?
(A) f grows asymptotically faster than g
(B) f grows asymptotically slower than g
(C) Both f(n) and g(n) are O(n^2)               Correct      
(D) g(n) is (n^2) but f(n) is not


Q34. In a singly linked list discussed in class:
(A) Insertion at the tail must be done in On)      
(B) Removing at the tail can be done in O(1)
(C) Removing at the head can be done in O(1)               Correct
(D) Finding the node in the middle of the list takes O(logn)


Q35. T(n) = 2T(n/2) + n
(a) O(1)
(b) O(n)
(c) O(n^2)
(d) O(logn)
(e) O(nlogn)               Correct      
(f) O(2^n)

Q36. T(n) = T(n/2) + 1
(a) O(1)
(b) O(n)
(c) O(n^2)
(d) O(logn)               Correct      
(e) O(nlogn)
(f) O(2^n)

Q37. T(n) = 2T(n - 1) + 1
(a) O(1)
(b) O(n)
(c) O(n^2)      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)               Correct

Q38. T(n) = T(n - 1) + 1
(a) O(1)
(b) O(n)               Correct      
(c) O(n^2)
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q39. T(n) = T(n) + 1.
(a) T(n) ≤ cn
(b) T(n - 1) ≤ c(n - 1)                Correct
(c) T(n - 1) ≤ cn - 1      
(d) T(n/2) ≤ cn/2

Q40. T(n) = T(n/2) + 1.
(a) T(n -1) ≤ c(n - 1)
(b) T(n) ≤ clogn
(c) T(n - 1) ≤ clog(n - 1)
(d) T(n/2) ≤ clog(n/2)               Correct      

Q41. T(n) = T(n/2) + n.
(a) T(n - 1) ≤ c(n - 1)
(b) T(n) ≤ cn logn
(c) T(n - 1) < c(n - 1) log(n - 1)
(d) T(n/2) ≤ c(n/2) log(n/2)               Correct      

Q42. What is the time complexity of the Selection Sort algorithm, supposing that the data size is n?
(a) O(1)
(b) O(n)
(c) O(n^2)               Correct      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q43. What is the time complexity of the Insertion Sort algorithm, supposing that the data size is n?
(a) O(1)
(b) O(n)
(c) O(n^2)               Correct      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q44. What is the time complexity of the following recursive function?
int someFunction(int n) {
    if (n == 0) return 1;
    else return n * someFunction(n-1);
}
(a) O(1)
(b) O(n)               Correct      
(c) O(n^2)
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q45. What is the time complexity of the following recursive function?
int x(int n):
    if (n <= 1) return 1;
    return 3 * x(n - 1)
(A) O(nlogn)
(B) O(3^n)
(C) O(n)               Correct      
(D) O(n^3)
(E) O(logn)

Q46. Which of the following algorithm discussed in class uses dynamic programming algorithm design strategy?
(A) Merge sort
(B) Insertion sort
(C) Fibonacci algorithm               Correct
(D) Binary search      
(E) Selection sort

Q47. Which of the following algorithm discussed in class uses divide and conquer algorithm design strategy?
(A) Merge sort               Correct      
(B) Binary search
(C) Insertion sort
(D) Fibonacci algorithm
(E) Selection sort

Q48. Given the following algorithm, suppose that S1=[1,3,6], and S2=[2,4,5].
public static void f(int[] S1, int[] S2, int[] S) {
    int i = 0; j =0;
    while( i +j < S.length) { 
        if (j == S2.length || (i < S1.length && S1[i] < S2[j])
          S[i+j] = S1[i++];
    else S1[j] = E2[j+1];
    }
}

What is the value in array S after the execution of the algorithm?
(A) 1 3 6 2 3 4 5      
(B) 1 3 6
(C) 2 3 4 5
(D) 1 2 3 3 4 5 6               Correct

Q49. What is the time complexity of the code above?
(a) O(1)
(b) O(n)               Correct
(c) O(n^2)      
(d) O(logn)
(e) O(nlogn)
(f) O(2^n)

Q50. In algorithm analysis, the base of log n is
(a) 2               Correct      
(b) 10
(c) e
(d) 4
(e) 1

Q51. Given the following algorithm:
int f(int n, int acc) {
    if (n == 0) return acc;
    return f(n - 1, n * acc);
}

Which of the following is NOT true?
(a) It computes factorial
(b) It is tail-recursive
(c) It is a linear program
(d) It is not tail recursive               Correct      

Q52. For the above function, which of the following computes factorial(n)?
(a) f(n,1)               Correct      
(b) f(n,0)
(c) f(1,n)
(d) f(0,n)

Q53. log xy is equal to
(a) log x log y      
(b) log x + log y               Correct
(c) x log y
(d) y log x

Q54. log x^2 is equal to
(a) 2log x               Correct      
(b) log x + 2
(c) x + 1
(d) 2x
(e) x^2

Q55. log 2^x is equal to
(a) x               Correct
(b) 2 log x
(c) log x + 2
(d) x + 1
(e) 2x      

Q56. Which of the following statements is FALSE?
(a) O(n) subset O(n^2)
(b) O(log n) subset O(n^2)
(c) O(n) subset O(log n)               Correct      
(d) O(1) subset O(logn)

Q57. Suppose that an algorithm has two nested loops: the outer runs n times, the inner runs n/2 times. What’s the time complexity?
(a) O(1)
(b) O(n)
(c) O(n^2)               Correct
(d) O(logn)      
(e) O(nlogn)
(f) O(2^n)

Q58. to Q60 are based on the following partial code for fast fibonacci function discussed in class.
public static long[] fib(int n) {
    if (n <= 1) {
    long[] answer = {n, 0};
    return answer;
}
long[] temp = [Q58];
long[] answer = {[Q59.], temp[0]};
return answer;
}

Q58. Answer for Q58. is
(a) fib(n-1)               Correct
(b) fib(n-1) + fib(n-2)
(c) fib(n-2)
(d) temp[0] + temp[1]      

Q59. Answer for Q59. is
(a) fib(n-1) + fib(n-2)      
(b) temp[1]
(c) fib(n-1)
(d) fib(n-2)
(e) temp[0] + temp[1]               Correct

Q60. The design strategy of the algorithm is

(a) divide and conquer
(b) dynamic programming               Correct      
(c) Greedy method
(d) Generate and test
