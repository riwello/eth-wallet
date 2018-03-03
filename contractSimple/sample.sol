pragma solidity ^0.4.0;
contract Sample {
    uint public value;

           function Sample(uint v) public {
                    value = v;
          }

          function set(uint v) public {
                    value = v;
          }

          function get() public constant returns (uint) {
                    return value;
          }
}