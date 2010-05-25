cedarsoft Serialization
====================

  cedarsoft Serialization offers version aware serialization of java object trees with maximum control.
  Its goal is to provide some simple classes (very small framework) that enables rapid development
  of versioned serialization.

  Documentation can be found at the [Maven generated site](http://serialization.cedarsoft.org).


Introduction
---------------------

  cedarsoft Serialization contains a plain interface that is implemented in several ways. The most commonly
  used backend implementation is based on Stax.
  This offers high performance serialization to XML. While offering maximum control to the developer the base
  classes contain most of the boiler plate code and offer a nice way to reuse the serializers.

  There exist several backends that can be used. The most commonly used backend is Stax-Mate.

### Resulting XML

  The resulting XML may be look like that:


    <?xml version="1.0" encoding="UTF-8"?>
    <businessObject xmlns="http://yourcompany.com/path/2.0.1">
      <name>theName</name>
      ...
    </businessObject>

  The second line contains a XML namespace declaration. Within this declaration the format version is encoded.
  If the XML format has to be changed, the version number is increased:


    <businessObject xmlns="http://yourcompany.com/path/2.0.2">


  The design of cedarsoft Serialization offers a very easy way to handle the old formats, too.



License
--------------------

  All files are released under the GPLv3 with Classpath Exception.

### Usage in commercial products

  cedarsoft Serialization may be used in commercial projects as long as the classes itself are not modified.
  The license contains the same Classpath Exception as Sun uses for
  the JDK.

  If necessary nontheless, commercial licenses are available. Send your request to [info@cedarsoft.de](mailto:info@cedarsoft.de).
