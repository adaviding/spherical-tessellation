# spherical-tessellation
This library leverages a particular form of spherical tessellation to help organize information geographically.

The sphere is tessellated into a hierarchy of equilateral spherical triangles.  The hierarchy can be accessed at any depth, and all triangles at a given depth are exactly the same size and shape.  This form of tessellation has many properties which are nice for developers who are trying to achieve one of the following things:

  1.  Perform calculations which are unbiased with respect to the location on the sphere's surface.
  2.  Create a geographical index which is efficient for searches based on spherical geomety.  For example:  Select data within 2 degrees of a given coordinate.

## status
My progress on this will be slow.

### java (maven)
Some of the mathematical code has been posted.

#### code style
The code style is slightly unorthodox for Java.

* I put the opening curly bracket (`{`) on a new line.
* I use constants which are `TitleCase` as opposed to `UPPER_CASE` because I think it looks better.
* The `st-numerics` module  mathematical code, so most of the fields are public (as opposed to private fields with public getters / setters).

Get over it.

### c#
No code has been posted yet.

### c++
No code has been posted yet.
