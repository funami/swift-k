#!/usr/bin/perl

# Convert the time unit from seconds to minutes
# INPUT:  A Swift log file with Unix time in the first columns
#                  timestamp ... 
#         where timestamp is a number
# OUTPUT: The same log file with the timestamp divided by 60

$load = 0;


while (<STDIN>) {
    @tokens = split;
    $tokens[0] /= 60.0;
    printf("@tokens\n");
}
