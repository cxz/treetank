/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */

package com.treetank.service.xml.xpath.functions;

/**
 * <h1>FuncDef</h1>
 * <p>
 * List of functions and their attributes according to the specification in <a
 * href="http://www.w3.org/TR/xquery-operators/"> XQuery 1.0 and XPath 2.0 Functions and Operators</a>.
 * </p>
 */
public enum FuncDef {

        /**
         * <p>
         * fn:node-name($arg as node()?) as xs:QName?
         * </p>
         * Returns an expanded-QName for node kinds that can have names. For other
         * kinds of nodes it returns the empty sequence. If $arg is the empty
         * sequence, the empty sequence is returned.
         */
        NODE_NAME("fn:node-name", AbstractFunction.class, 1, 1, "xs:QName"),

        /**
         * <p>
         * fn:nilled : fn:nilled($arg as node()?) as xs:boolean?
         * </p>
         * Returns an xs:boolean indicating whether the argument node is "nilled".
         * If the argument is not an element node, returns the empty sequence. If
         * the argument is the empty sequence, returns the empty sequence.
         */
        NILLED("fn:nilled", AbstractFunction.class, 1, 1, "xs:boolean"),

        /**
         * <p>
         * fn:string() as xs:string
         * </p>
         * <p>
         * fn:string($arg as item()?) as xs:string
         * </p>
         * <p>
         * Returns the value of $arg represented as a xs:string. If no argument is supplied, the context item
         * (.) is used as the default argument. The behavior of the function if the argument is omitted is
         * exactly the same as if the context item had been passed as the argument.
         * </p>
         */
        STRING("fn:string", FNString.class, 0, 1, "xs:string"),

        /**
         * <p>
         * fn:data($arg as item()*) as xs:anyAtomicType*
         * </p>
         * <p>
         * fn:data takes a sequence of items and returns a sequence of atomic values.
         */
        DATA("fn:data", AbstractFunction.class, 1, 1, "xs:anyAtomicType"),

        /**
         * <p>
         * fn:base-uri() as xs:anyURI?
         * </p>
         * <p>
         * fn:base-uri($arg as node()?) as xs:anyURI?
         * </p>
         * <p>
         * Returns the value of the base-uri URI property for $arg as defined by the accessor function
         * dm:base-uri() for that kind of node in Section 5.2 base-uri AccessorDM.
         * </p>
         */
        BASE_URI("fn:base-uri", AbstractFunction.class, 0, 1, "xs:anyURI"),

        /**
         * <p>
         * fn:document-uri($arg as node()?) as xs:anyURI?
         * </p>
         * <p>
         * Returns the value of the document-uri property for $arg as defined by the dm:document-uri accessor
         * function defined in Section 6.1.2 AccessorsDM.
         * </p>
         */
        DOC_URI("fn:document-uri", AbstractFunction.class, 1, 1, "xs:anyURI"),

        // /**
        // * <p>
        // * fn:error() as none
        // * </p>
        // * <p>
        // * fn:error($error as xs:QName) as none
        // * </p>
        // * <p>
        // * fn:error($error as xs:QName?, $description as xs:string) as none
        // * </p>
        // * <p>
        // * fn:error( $error as xs:QName?, $description as xs:string, $error-object
        // as
        // * item()*) as none
        // * </p>
        // * <p>
        // * The fn:error function raises an error. While this function never
        // returns
        // a
        // * value, an error is returned to the external processing environment as
        // an
        // * xs:anyURI or an xs:QName. The error xs:anyURI is derived from the error
        // * xs:QName. An error xs:QName with namespace URI NS and local part LP
        // will
        // be
        // * returned as the xs:anyURI NS#LP. The method by which the xs:anyURI or
        // * xs:QName is returned to the external processing environment is
        // * implementation dependent�
        // * </p>
        // */
        // ERROR("fn:error", AbstractFunction.class, 0, 3),
        //
        // /**
        // * <p>
        // * fn:trace($value as item()*, $label as xs:string) as item()*
        // * </p>
        // * <p>
        // * Provides an execution trace intended to be used in debugging queries.
        // * </p>
        // */
        // TRACE("fn:trace", 1, 2),
        //
        // /**
        // * <p>
        // * op:numeric-add($arg1 as numeric, $arg2 as numeric) as numeric.
        // * </p>
        // * <p>
        // * Backs up the "+" operator and returns the arithmetic sum of its
        // operands
        // * </p>
        // */
        // N_ADD("op:numeric-add", 2, 2),
        //
        // /**
        // * <p>
        // * op:numeric-subtract($arg1 as numeric, $arg2 as numeric) as numeric
        // * </p>
        // * <p>
        // * Backs up the "-" operator and returns the arithmetic difference of its
        // * operands: ($arg1 - $arg2).
        // * </p>
        // */
        // N_SUB("op:numeric-substract", 2, 2),
        //
        // /**
        // * <p>
        // * op:numeric-multiply($arg1 as numeric, $arg2 as numeric) as numeric
        // * </p>
        // * <p>
        // * Backs up the "*" operator and returns the arithmetic product of its
        // * operands: ($arg1 * $arg2).
        // * </p>
        // */
        // N_MUL("op:numeric-multiply", 2, 2),
        //
        // /**
        // * <p>
        // * op:numeric-divide($arg1 as numeric, $arg2 as numeric) as numeric
        // * </p>
        // * <p>
        // * Backs up the "div" operator and returns the arithmetic quotient of its
        // * operands: ($arg1 div $arg2).
        // * </p>
        // */
        // N_DIV("op:numeric-divide", 2, 2),
        //
        // /**
        // * <p>
        // * op:numeric-integer-divide($arg1 as numeric, $arg2 as numeric) as
        // xs:integer
        // * </p>
        // * <p>
        // * This function backs up the "idiv" operator and performs an integer
        // * division: that is, it divides the first argument by the second, and
        // returns
        // * the integer obtained by truncating the fractional part of the result.
        // The
        // * division is performed so that the sign of the fractional part is the
        // same
        // * as the sign of the dividend.
        // * </p>
        // */
        // N_IDIV("op:numeric_interger-divide", 2, 2),
        //
        // /**
        // * <p>
        // * op:numeric-mod($arg1 as numeric, $arg2 as numeric) as numeric
        // * </p>
        // * <p>
        // * Backs up the "mod" operator. Informally, this function returns the
        // * remainder resulting from dividing $arg1, the dividend, by $arg2, the
        // * divisor. The operation a mod b for operands that are xs:integer or
        // * xs:decimal, or types derived from them, produces a result such that (a
        // idiv
        // * b)*b+(a mod b) is equal to a and the magnitude of the result is always
        // less
        // * than the magnitude of b. This identity holds even in the special case
        // that
        // * the dividend is the negative integer of largest possible magnitude for
        // its
        // * type and the divisor is -1 (the remainder is 0). It follows from this
        // rule
        // * that the sign of the result is the sign of the dividend.
        // * </p>
        // */
        // N_MOD("op:numeric:mod", 2, 2),
        //
        // /**
        // * <p>
        // * op:numeric-unary-plus($arg as numeric) as numeric
        // * </p>
        // * <p>
        // * Backs up the unary "+" operator and returns its operand with the sign
        // * unchanged: (+ $arg). Semantically, this operation performs no
        // operation.
        // * </p>
        // */
        // N_PLUS("op:numeric-unary-plus", 1, 1),
        //
        // /**
        // * <p>
        // * op:numeric-unary-minus($arg as numeric) as numeric
        // * </p>
        // * <p>
        // * Backs up the unary "-" operator and returns its operand with the sign
        // * reversed: (- $arg). If $arg is positive, its negative is returned; if
        // it
        // is
        // * negative, its positive is returned.
        // * </p>
        // */
        // N_MINUS("op:numeric-unary-minus", 1, 1),
        //
        /**
         * <p>
         * op:numeric-equal($arg1 as numeric, $arg2 as numeric) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 is equal to the value of $arg2.
         * </p>
         * <p>
         * This function backs up the "eq", "ne", "le" and "ge" operators on numeric values.
         * </p>
         */
        N_EQ("op:numeric-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:numeric-less-than($arg1 as numeric, $arg2 as numeric) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if $arg1 is less than $arg2.
         * </p>
         * <p>
         * This function backs up the "lt" and "le" operators on numeric values.
         * </p>
         */
        N_LT("op:numeric-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:numeric-greater-than($arg1 as numeric, $arg2 as numeric) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if $arg1 is greater than $arg2. F
         * </p>
         */
        N_GT("op:numeric-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        // /**
        // * <p>
        // * fn:abs($arg as numeric?) as numeric?
        // * </p>
        // * <p>
        // * Returns the absolute value of $arg.
        // * </p>
        // */
        // ABS("fn:abs", 1, 1),
        //
        // /**
        // * <p>
        // * fn:ceiling($arg as numeric?) as numeric?
        // * </p>
        // * <p>
        // * Returns the smallest (closest to negative infinity) number with no
        // * fractional part that is not less than the value of $arg.
        // * </p>
        // */
        // CEIL("fn:ceiling", 1, 1),
        //
        // /**
        // * <p>
        // * fn:floor($arg as numeric?) as numeric?
        // * </p>
        // * <p>
        // * Returns the largest (closest to positive infinity) number with no
        // * fractional part that is not greater than the value of $arg.
        // * </p>
        // */
        // FLOOR("fn:floor", 1, 1),
        //
        // /**
        // * <p>
        // * fn:round($arg as numeric?) as numeric?
        // * </p>
        // * <p>
        // * Returns the number with no fractional part that is closest to the
        // argument.
        // * If there are two such numbers, then the one that is closest to positive
        // * infinity is returned. If type of $arg is one of the four numeric types
        // * xs:float, xs:double, xs:decimal or xs:integer the type of the result is
        // the
        // * same as the type of $arg. If the type of $arg is a type derived from
        // one
        // of
        // * the numeric types, the result is an instance of the base numeric type.
        // */
        // ROUND("fn:round", 1, 1),
        //
        // /**
        // * <p>
        // * fn:round-half-to-even($arg as numeric?) as numeric?
        // * </p>
        // * <p>
        // * fn:round-half-to-even($arg as numeric?, $precision as xs:integer) as
        // * numeric?
        // * </p>
        // * <p>
        // * The value returned is the nearest (that is, numerically closest) value
        // to
        // * $arg that is a multiple of ten to the power of minus $precision. If two
        // * such values are equally near (e.g. if the fractional part in $arg is
        // * exactly .500...), the function returns the one whose least significant
        // * digit is even.
        // */
        // ROUND_EVEN("fn:round-half-to-even", 1, 2),
        //
        /**
         * <p>
         * fn:codepoints-to-string($arg as xs:integer*) as xs:string
         * </p>
         * <p>
         * Creates an xs:string from a sequence of [The Unicode Standard] code points. Returns the zero-length
         * string if $arg is the empty sequence. If any of the code points in $arg is not a legal XML
         * character, an error is raised [err:FOCH0001].
         * </p>
         */
        CODEPNT2STR("fn:codepoints-to-string", AbstractFunction.class, 0, 1, "xs:string"),

        /**
         * <p>
         * fn:string-to-codepoints($arg as xs:string?) as xs:integer*
         * </p>
         * <p>
         * Returns the sequence of [The Unicode Standard] code points that constitute an xs:string. If $arg is
         * a zero-length string or the empty sequence, the empty sequence is returned.
         */
        STR2CODEPNT("fn:string-to-codepoints", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?) as xs:integer?
         * </p>
         * <p>
         * fn:compare( $comparand1 as xs:string?, $comparand2 as xs:string?, $collation as xs:string) as
         * xs:integer?
         * </p>
         * <p>
         * Returns -1, 0, or 1, depending on whether the value of the $comparand1 is respectively less than,
         * equal to, or greater than the value of $comparand2, according to the rules of the collation that is
         * used.
         * <p>
         */
        COMP("fn:compare", AbstractFunction.class, 2, 3, "xs:integer"),

        /**
         * <p>
         * fn:codepoint-equal( $comparand1 as xs:string?, $comparand2 as xs:string?) as xs:boolean?
         * </p>
         * <p>
         * Returns true or false depending on whether the value of $comparand1 is equal to the value of
         * $comparand2, according to the Unicode code point collation
         * (http://www.w3.org/2005/xpath-functions/collation/codepoint).
         * </p>
         */
        CODEPNT_EQ("fn:codepoint-equal", AbstractFunction.class, 2, 2, "xs:string"),

        /**
         * <p>
         * fn:concat( $arg1 as xs:anyAtomicType?, $arg2 as xs:anyAtomicType?, ... ) as xs:string
         * </p>
         * <p>
         * Accepts two or more xs:anyAtomicType arguments and casts them to xs:string. Returns the xs:string
         * that is the concatenation of the values of its arguments after conversion. If any of the arguments
         * is the empty sequence, the argument is treated as the zero-length string.
         * </p>
         */
        CONCAT("fn:concat", AbstractFunction.class, 2, Integer.MAX_VALUE, "xs:string"),

        /**
         * <p>
         * fn:string-join($arg1 as xs:string*, $arg2 as xs:string) as xs:string
         * </p>
         * <p>
         * Returns a xs:string created by concatenating the members of the $arg1 sequence using $arg2 as a
         * separator. If the value of $arg2 is the zero-length string, then the members of $arg1 are
         * concatenated without a separator.
         * </p>
         */
        STRJOIN("fn:string-join", AbstractFunction.class, 2, 2, "xs:string"),

        /**
         * <p>
         * fn:substring( $sourceString as xs:string?, $startingLoc as xs:double) as xs:string
         * </p>
         * <p>
         * fn:substring( $sourceString as xs:string?, $startingLoc as xs:double, $length as xs:double) as
         * xs:string
         * </p>
         * <p>
         * Returns the portion of the value of $sourceString beginning at the position indicated by the value
         * of $startingLoc and continuing for the number of characters indicated by the value of $length. The
         * characters returned do not extend beyond $sourceString. If $startingLoc is zero or negative, only
         * those characters in positions greater than zero are returned.
         * </p>
         */
        SUBSTR("fn:substring", AbstractFunction.class, 2, 3, "xs:string"),

        /**
         * <p>
         * fn:string-length() as xs:integer
         * </p>
         * <p>
         * fn:string-length($arg as xs:string?) as xs:integer
         * </p>
         * <p>
         * Returns an xs:integer equal to the length in characters of the value of $arg.
         * <p>
         */
        STRLEN("fn:string-length", AbstractFunction.class, 0, 1, "xs:integer"),

        /**
         * <p>
         * fn:normalize-space() as xs:string
         * </p>
         * <p>
         * fn:normalize-space($arg as xs:string?) as xs:string
         * </p>
         * <p>
         * Returns the value of $arg with whitespace normalized by stripping leading and trailing whitespace
         * and replacing sequences of one or more than one whitespace character with a single space, #x20.
         */
        NORM_SPACE("fn:normalize-space", AbstractFunction.class, 0, 1, "xs:string"),

        /**
         * <p>
         * fn:normalize-unicode($arg as xs:string?) as xs:string
         * </p>
         * <p>
         * fn:normalize-unicode( $arg as xs:string?, $normalizationForm as xs:string) as xs:string
         * </p>
         * <p>
         * Returns the value of $arg normalized according to the normalization criteria for a normalization
         * form identified by the value of $normalizationForm. The effective value of the $normalizationForm
         * is computed by removing leading and trailing blanks, if present, and converting to upper case.
         */
        NORM_UNI("fn:normalize-unicode", AbstractFunction.class, 1, 2, "xs:string"),

        /**
         * <p>
         * fn:upper-case($arg as xs:string?) as xs:string
         * </p>
         * <p>
         * Returns the value of $arg after translating every character to its upper-case correspondent as
         * defined in the appropriate case mappings section in the Unicode standard [The Unicode Standard].
         * For versions of Unicode beginning with the 2.1.8 update, only locale-insensitive case mappings
         * should be applied. Beginning with version 3.2.0 (and likely future versions) of Unicode, precise
         * mappings are described in default case operations, which are full case mappings in the absence of
         * tailoring for particular languages and environments. Every lower-case character that does not have
         * an upper-case correspondent, as well as every upper-case character, is included in the returned
         * value in its original form.
         * </p>
         */
        UP_CASE("fn:upper-case", AbstractFunction.class, 1, 1, "xs:string"),

        /**
         * <p>
         * fn:lower-case($arg as xs:string?) as xs:string
         * </p>
         * <p>
         * Returns the value of $arg after translating every character to its lower-case correspondent as
         * defined in the appropriate case mappings section in the Unicode standard [The Unicode Standard].
         * For versions of Unicode beginning with the 2.1.8 update, only locale-insensitive case mappings
         * should be applied. Beginning with version 3.2.0 (and likely future versions) of Unicode, precise
         * mappings are described in default case operations, which are full case mappings in the absence of
         * tailoring for particular languages and environments. Every upper-case character that does not have
         * a lower-case correspondent, as well as every lower-case character, is included in the returned
         * value in its original form.
         * </p>
         */
        LOW_CASE("fn:lower-case", AbstractFunction.class, 1, 1, "xs:string"),

        /**
         * <p>
         * fn:translate( $arg as xs:string?, $mapString as xs:string, $transString as xs:string) as xs:string
         * </p>
         * <p>
         * Returns the value of $arg modified so that every character in the value of $arg that occurs at some
         * position N in the value of $mapString has been replaced by the character that occurs at position N
         * in the value of $transString.
         * </p>
         */
        TRANSLATE("fn:translate", AbstractFunction.class, 3, 3, "xs:string"),

        /**
         * <p>
         * fn:encode-for-uri($uri-part as xs:string?) as xs:string
         * </p>
         * <p>
         * This function encodes reserved characters in an xs:string that is intended to be used in the path
         * segment of a URI. It is invertible but not idempotent. This function applies the URI escaping rules
         * defined in section 2 of [RFC 3986] to the xs:string supplied as $uri-part. The effect of the
         * function is to escape reserved characters. Each such character in the string is replaced with its
         * percent-encoded form as described in [RFC 3986].
         * </p>
         */
        ENCODE_URI("fn:encode-for-uri", AbstractFunction.class, 1, 1, "xs:string"),

        /**
         * <p>
         * fn:iri-to-uri($iri as xs:string?) as xs:string
         * </p>
         * <p>
         * This function converts an xs:string containing an IRI into a URI according to the rules spelled out
         * in Section 3.1 of [RFC 3987]. It is idempotent but not invertible.
         * </p>
         */
        IRI2URI("fn:iri-to-uri", AbstractFunction.class, 1, 1, "xs:string"),

        /**
         * <p>
         * fn:escape-html-uri($uri as xs:string?) as xs:string
         * </p>
         * This function escapes all characters except printable characters of the
         * US-ASCII coded character set, specifically the octets ranging from 32 to
         * 126 (decimal). The effect of the function is to escape a URI in the
         * manner html user agents handle attribute values that expect URIs. Each
         * character in $uri to be escaped is replaced by an escape sequence, which
         * is formed by encoding the character as a sequence of octets in UTF-8, and
         * then representing each of these octets in the form %HH, where HH is the
         * hexadecimal representation of the octet. This function must always
         * generate hexadecimal values using the upper-case letters A-F.
         */
        ESCAPE_HTML_URI("fn:escape-html-uri", AbstractFunction.class, 1, 1, "xs:string"),

        /**
         * <p>
         * fn:contains($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean
         * <p>
         * fn:contains( $arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean
         * </p>
         * Returns an xs:boolean indicating whether or not the value of $arg1
         * contains (at the beginning, at the end, or anywhere within) at least one
         * sequence of collation units that provides a minimal match to the
         * collation units in the value of $arg2, according to the collation that is
         * used.
         */
        CONTAINS("fn:contains", AbstractFunction.class, 2, 3, "xs:boolean"),

        /**
         * <p>
         * fn:starts-with($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean
         * <p>
         * fn:starts-with( $arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean
         * </p>
         * <p>
         * Returns an xs:boolean indicating whether or not the value of $arg1 starts with a sequence of
         * collation units that provides a minimal match to the collation units of $arg2 according to the
         * collation that is used.
         * <p>
         */
        STARTS_WITH("fn:starts-with", AbstractFunction.class, 2, 3, "xs:boolean"),

        /**
         * <p>
         * fn:ends-with($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean
         * <p>
         * fn:ends-with( $arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean </p
         * <p>
         * >Returns an xs:boolean indicating whether or not the value of $arg1 ends with a sequence of
         * collation units that provides a minimal match to the collation units of $arg2 according to the
         * collation that is used.
         * <p>
         */
        ENDS_WITH("fn:ends-with", AbstractFunction.class, 2, 3, "xs:boolean"),
        /**
         * <p>
         * fn:substring-before($arg1 as xs:string?, $arg2 as xs:string?) as xs:string
         * <p>
         * fn:substring-before( $arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as
         * xs:string
         * </p>
         * <p>
         * Returns the substring of the value of $arg1 that precedes in the value of $arg1 the first
         * occurrence of a sequence of collation units that provides a minimal match to the collation units of
         * $arg2 according to the collation that is used.
         * <p>
         */
        SUBSTR_BEFORE("fn:substring-before", AbstractFunction.class, 2, 3, "xs:string"),

        /**
         * <p>
         * fn:substring-after($arg1 as xs:string?, $arg2 as xs:string?) as xs:string
         * <p>
         * fn:substring-after( $arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:string
         * </p>
         * <p>
         * Returns the substring of the value of $arg1 that follows in the value of $arg1 the first occurrence
         * of a sequence of collation units that provides a minimal match to the collation units of $arg2
         * according to the collation that is used.
         * <p>
         */
        SUBSTR_AFTER("fn:substring-after", AbstractFunction.class, 2, 3, "xs:string"),

        /**
         * <p>
         * fn:matches($input as xs:string?, $pattern as xs:string) as xs:boolean
         * </p>
         * <p>
         * fn:matches( $input as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:boolean
         * </p>
         * <p>
         * The function returns true if $input matches the regular expression supplied as $pattern as
         * influenced by the value of $flags, if present; otherwise, it returns false.
         * <p>
         */
        MATCH("fn:matches", AbstractFunction.class, 2, 3, "xs:boolean"),

        /**
         * <p>
         * fn:replace( $input as xs:string?, $pattern as xs:string, $replacement as xs:string) as xs:string
         * </p>
         * <p>
         * fn:replace( $input as xs:string?, $pattern as xs:string, $replacement as xs:string, $flags as
         * xs:string) as xs:string
         * </p>
         * <p>
         * The function returns the xs:string that is obtained by replacing each non-overlapping substring of
         * $input that matches the given $pattern with an occurrence of the $replacement string.
         * </p>
         */
        REPLACE("fn:replace", AbstractFunction.class, 3, 4, "xs:string"),

        /**
         * <p>
         * fn:tokenize($input as xs:string?, $pattern as xs:string) as xs:string*
         * </p>
         * <p>
         * fn:tokenize( $input as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:string*
         * </p>
         * <p>
         * This function breaks the $input string into a sequence of strings, treating any substring that
         * matches $pattern as a separator. The separators themselves are not returned.
         * </p>
         */
        TOKENIZE("fn:tokenize", AbstractFunction.class, 2, 3, "xs:string"),
        /**
         * <p>
         * fn:resolve-uri($relative as xs:string?) as xs:anyURI?
         * </p>
         * <p>
         * fn:resolve-uri($relative as xs:string?, $base as xs:string) as xs:anyURI?
         * </p>
         * <p>
         * The purpose of this function is to enable a relative URI to be resolved against an absolute URI.
         * </p>
         */
        RESOLVE_URI("fn:resolve_uri", AbstractFunction.class, 1, 2, "xs:string"),

        /**
         * <p>
         * fn:true() as xs:boolean
         * </p>
         * Returns the xs:boolean value true. Equivalent to xs:boolean("1").
         */
        TRUE("fn:true", AbstractFunction.class, 0, 0, "xs:boolean"),

        /**
         * <p>
         * fn:false() as xs:boolean
         * </p>
         * Returns the xs:boolean value false. Equivalent to xs:boolean("0").
         */
        FALSE("fn:false", AbstractFunction.class, 0, 0, "xs:boolean"),

        /**
         * <p>
         * op:boolean-equal($value1 as xs:boolean, $value2 as xs:boolean) as xs:boolean
         * </p>
         * Returns true if both arguments are true or if both arguments are false.
         * Returns false if one of the arguments is true and the other argument is
         * false.
         */
        B_EQ("op:boolean-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:boolean-less-than($arg1 as xs:boolean, $arg2 as xs:boolean) as xs:boolean
         * </p>
         * <p>
         * Returns true if $arg1 is false and $arg2 is true. Otherwise, returns false.
         */
        B_LT("op:boolean-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:boolean-greater-than($arg1 as xs:boolean, $arg2 as xs:boolean) as xs:boolean
         * </p>
         * <p>
         * Returns true if $arg1 is true and $arg2 is false. Otherwise, returns false.
         * </p>
         */
        B_GT("op:boolean-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * fn:not($arg as item()*) as xs:boolean
         * </p>
         * <p>
         * $arg is first reduced to an effective boolean value by applying the fn:boolean() function. Returns
         * true if the effective boolean value is false, and false if the effective boolean value is true.;
         * </p>
         */
        NOT("fn:not", FNNot.class, 1, 1, "xs:boolean"),

        /**
         * <p>
         * op:yearMonthDuration-less-than( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as
         * xs:boolean
         * </p>
         * <p>
         * Returns true if and only if $arg1 is less than $arg2. Returns false otherwise.
         * </p>
         */
        YMD_LT("op:yearMonthDuration-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:yearMonthDuration-greater-than( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as
         * xs:boolean
         * </p>
         * <p>
         * Returns true if and only if $arg1 is greater than $arg2. Returns false otherwise.
         * </p>
         */
        YMD_GT("op:yearMonthDuration-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:dayTimeDuration-less-than( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as
         * xs:boolean
         * </p>
         * <p>
         * Returns true if and only if $arg1 is less than $arg2. Returns false otherwise.
         * </p>
         */
        DTD_LT("op:dayTimeDuration-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:dayTimeDuration-greater-than( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as
         * xs:boolean
         * </p>
         * <p>
         * Returns true if and only if $arg1 is greater than $arg2. Returns false otherwise.
         * </p>
         */
        DTD_GT("op:dayTimeDuration-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:duration-equal($arg1 as xs:duration, $arg2 as xs:duration) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the xs:yearMonthDuration and the xs:dayTimeDuration components of $arg1
         * and $arg2 compare equal respectively. Returns false otherwise.
         * </p>
         */
        DUR_EQ("op:duration-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:dateTime-equal($arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 is equal to the value of $arg2 according to the
         * algorithm defined in section 3.2.7.4 of [XML Schema Part 2: Datatypes Second Edition]
         * "Order relation on dateTime" for xs:dateTime values with timezones. Returns false otherwise.
         * </p>
         */
        DT_EQ("op:dateTime-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:dateTime-less-than($arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 is less than the value of $arg2 according to the
         * algorithm defined in section 3.2.7.4 of [XML Schema Part 2: Datatypes Second Edition]
         * "Order relation on dateTime" for xs:dateTime values with timezones. Returns false otherwise.
         * </p>
         */
        DT_LT("p:dateTime-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:dateTime-greater-than( $arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 is greater than the value of $arg2 according to the
         * algorithm defined in section 3.2.7.4 of [XML Schema Part 2: Datatypes Second Edition]
         * "Order relation on dateTime" for xs:dateTime values with timezones. Returns false otherwise.
         * </p>
         */
        DT_GT("op:dateTime-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:date-equal($arg1 as xs:date, $arg2 as xs:date) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the starting instant of $arg1 is equal to starting instant of $arg2.
         * Returns false otherwise.
         * </p>
         */
        DA_EQ("op:date-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:date-less-than($arg1 as xs:date, $arg2 as xs:date) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the starting instant of $arg1 is less than the starting instant of
         * $arg2. Returns false otherwise.
         * </p>
         */
        DA_LT("op:date-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:date-greater-than($arg1 as xs:date, $arg2 as xs:date) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the starting instant of $arg1 is greater than the starting instant of
         * $arg2. Returns false otherwise.
         * </p>
         */
        DA_GT("op:date-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:time-equal($arg1 as xs:time, $arg2 as xs:time) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 converted to an xs:dateTime using the date
         * components from the reference xs:dateTime is equal to the value of $arg2 converted to an
         * xs:dateTime using the date components from the same reference xs:dateTime. Returns false otherwise.
         * </p>
         */
        T_EQ("op:time-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:time-less-than($arg1 as xs:time, $arg2 as xs:time) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 converted to an xs:dateTime using the date
         * components from the reference xs:dateTime is less than the normalized value of $arg2 converted to
         * an xs:dateTime using the date components from the same reference xs:dateTime. Returns false
         * otherwise.
         * </p>
         */
        T_LT("op:time-less-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:time-greater-than($arg1 as xs:time, $arg2 as xs:time) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the value of $arg1 converted to an xs:dateTime using the date
         * components from the reference xs:dateTime is greater than the value of $arg2 converted to an
         * xs:dateTime using the date components from the same reference xs:dateTime. Returns false otherwise.
         * </p>
         */
        T_GT("op:time-greater-than", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:gYearMonth-equal( $arg1 as xs:gYearMonth, $arg2 as xs:gYearMonth) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the xs:dateTimes representing the starting instants of $arg1 and $arg2
         * compare equal. The starting instants of $arg1 and $arg2 are calculated by adding the missing
         * components of $arg1 and $arg2 from the xs:dateTime template xxxx-xx-ddT00:00:00 where dd represents
         * the last day of the month component in $arg1 or $arg2. Returns false otherwise.
         * </p>
         */
        GYM_EQ("op:gYearMonth-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:gYear-equal($arg1 as xs:gYear, $arg2 as xs:gYear) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the xs:dateTimes representing the starting instants of $arg1 and $arg2
         * compare equal. The starting instants of $arg1 and $arg2 are calculated by adding the missing
         * components of $arg1 and $arg2 from a xs:dateTime template such as xxxx-01-01T00:00:00. Returns
         * false otherwise.
         * </p>
         */
        GY_EQ("op:gYear-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:gMonthDay-equal($arg1 as xs:gMonthDay, $arg2 as xs:gMonthDay) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the xs:dateTimes representing the starting instants of equivalent
         * occurrences of $arg1 and $arg2 compare equal. The starting instants of equivalent occurrences of
         * $arg1 and $arg2 are calculated by adding the missing components of $arg1 and $arg2 from an
         * xs:dateTime template such as 1972-xx-xxT00:00:00. Returns false otherwise.
         * </p>
         */
        GMD_EQ("op:gMonthDay-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:gMonth-equal($arg1 as xs:gMonth, $arg2 as xs:gMonth) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the xs:dateTimes representing the starting instants of equivalent
         * occurrences of $arg1 and $arg2 compare equal. The starting instants of equivalent occurrences of
         * $arg1 and $arg2 are calculated by adding the missing components of $arg1 and $arg2 from an
         * xs:dateTime template such as 1972-xx-ddT00:00:00 where dd represents the last day of the month
         * component in $arg1 or $arg2. Returns false otherwise.
         * </p>
         */
        GM_EQ("op:gMonth-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:gDay-equal($arg1 as xs:gDay, $arg2 as xs:gDay) as xs:boolean
         * </p>
         * <p>
         * Returns true if and only if the xs:dateTimes representing the starting instants of equivalent
         * occurrences of $arg1 and $arg2 compare equal. The starting instants of equivalent occurrences of
         * $arg1 and $arg2 are calculated by adding the missing components of $arg1 and $arg2 from an
         * xs:dateTime template such as 1972-12-xxT00:00:00. Returns false otherwise.
         * </p>
         */
        GD_EQ("op:gDay-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * fn:months-from-duration($arg as xs:duration?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the months component in the value of $arg. The result is
         * obtained by casting $arg to an xs:yearMonthDuration (see 17.1.4 Casting to duration types) and then
         * computing the months component as described in 10.3.1.3 Canonical representation.
         * </p>
         */
        MON_FROM_DUR("fn:months-from-duration", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:years-from-duration($arg as xs:duration?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the years component in the value of $arg. The result is obtained
         * by casting $arg to an xs:yearMonthDuration (see 17.1.4 Casting to duration types) and then
         * computing the years component as described in 10.3.1.3 Canonical representation.
         * </p>
         */
        YEAR_FROM_DUR("fn:years-from-duration", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:days-from-duration($arg as xs:duration?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the days component in the value of $arg. The result is obtained
         * by casting $arg to an xs:dayTimeDuration (see 17.1.4 Casting to duration types) and then computing
         * the days component as described in 10.3.2.3 Canonical representation.
         * </p>
         */
        DAYS_FROM_DUR("fn:days-from-duration", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:hours-from-duration($arg as xs:duration?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the hours component in the value of $arg. The result is obtained
         * by casting $arg to an xs:dayTimeDuration (see 17.1.4 Casting to duration types) and then computing
         * the hours component as described in 10.3.2.3 Canonical representation.
         * </p>
         */
        HOURS_FROM_DUR("fn:hours-from-duration", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:minutes-from-duration($arg as xs:duration?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the minutes component in the value of $arg. The result is
         * obtained by casting $arg to an xs:dayTimeDuration (see 17.1.4 Casting to duration types) and then
         * computing the minutes component as described in 10.3.2.3 Canonical representation.
         * </p>
         */
        MIN_FROM_DUR("fn:minutes-from-duration", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:seconds-from-duration($arg as xs:duration?) as xs:decimal?
         * </p>
         * <p>
         * Returns an xs:decimal representing the seconds component in the value of $arg. The result is
         * obtained by casting $arg to an xs:dayTimeDuration (see 17.1.4 Casting to duration types) and then
         * computing the seconds component as described in 10.3.2.3 Canonical representation.
         * </p>
         */
        SEC_FROM_DUR("fn:seconds-from-duration", AbstractFunction.class, 1, 1, "xs:decimal"),

        /**
         * <p>
         * fn:year-from-dateTime($arg as xs:dateTime?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the year component in the localized value of $arg. The result
         * may be negative.
         * </p>
         */
        YEAR_FROM_DT("fn:year-from-dateTime", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:month-from-dateTime($arg as xs:dateTime?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the
         * localized value of $arg.
         * </p>
         */
        MON_FROM_DT("fn:month-from-dateTime", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:day-from-dateTime($arg as xs:dateTime?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the
         * localized value of $arg.
         * </p>
         */
        DAY_FROM_DT("fn:day-from-dateTime", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:hours-from-dateTime($arg as xs:dateTime?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer between 0 and 23, both inclusive, representing the hours component in the
         * localized value of $arg.
         * </p>
         */
        HOURS_FROM_DT("fn:hours-from-dateTime", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:minutes-from-dateTime($arg as xs:dateTime?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer value between 0 and 59, both inclusive, representing the minute component in
         * the localized value of $arg.
         * </p>
         */
        MIN_FROM_DT("fn:minutes-from-dateTime", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:seconds-from-dateTime($arg as xs:dateTime?) as xs:decimal?
         * </p>
         * <p>
         * Returns an xs:decimal value greater than or equal to zero and less than 60, representing the
         * seconds and fractional seconds in the localized value of $arg.
         * </p>
         */
        SEC_FROM_DT("fn:seconds-from-dateTime", AbstractFunction.class, 1, 1, "xs:decimal"),

        /**
         * <p>
         * fn:timezone-from-dateTime($arg as xs:dateTime?) as xs:dayTimeDuration?
         * </p>
         * <p>
         * Returns the timezone component of $arg if any. If $arg has a timezone component, then the result is
         * an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00
         * hours, both inclusive. Otherwise, the result is the empty sequence.
         * </p>
         */
        TZ_FROM_DT("fn:timezone-from-dateTime", AbstractFunction.class, 1, 1, "xs:dayTimeDuration"),

        /**
         * <p>
         * fn:year-from-date($arg as xs:date?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer representing the year in the localized value of $arg. The value may be
         * negative.
         * </p>
         */
        YEAR_FROM_DATE("fn:year-from-date", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:month-from-date($arg as xs:date?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the
         * localized value of $arg.
         * </p>
         */
        MON_FROM_DATE("fn:month-from-date", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:day-from-date($arg as xs:date?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the
         * localized value of $arg.
         * </p>
         */
        DAY_FROM_DATE("fn:day-from-date", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:timezone-from-date($arg as xs:date?) as xs:dayTimeDuration?
         * </p>
         * <p>
         * Returns the timezone component of $arg if any. If $arg has a timezone component, then the result is
         * an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00
         * hours, both inclusive. Otherwise, the result is the empty sequence.
         * </p>
         */
        TZ_FROM_DATE("fn:timezone-from-date", AbstractFunction.class, 1, 1, "xs:dayTimeDuration"),

        /**
         * <p>
         * fn:hours-from-time($arg as xs:time?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer between 0 and 23, both inclusive, representing the value of the hours
         * component in the localized value of $arg.
         * </p>
         */
        HOURS_FROM_TIME("fn:hours-from-time", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:minutes-from-time($arg as xs:time?) as xs:integer?
         * </p>
         * <p>
         * Returns an xs:integer value between 0 and 59, both inclusive, representing the value of the minutes
         * component in the localized value of $arg.
         * </p>
         */
        MIN_FROM_TIME("fn:minutes-from-time", AbstractFunction.class, 1, 1, "xs:integer"),

        /**
         * <p>
         * fn:seconds-from-time($arg as xs:time?) as xs:decimal?
         * </p>
         * <p>
         * Returns an xs:decimal value greater than or equal to zero and less than 60, representing the
         * seconds and fractional seconds in the localized value of $arg.
         * </p>
         */
        SEC_FROM_TIME("fn:seconds-from-time", AbstractFunction.class, 1, 1, "xs:decimal"),

        /**
         * <p>
         * fn:timezone-from-time($arg as xs:time?) as xs:dayTimeDuration?
         * </p>
         * <p>
         * Returns the timezone component of $arg if any. If $arg has a timezone component, then the result is
         * an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00
         * hours, both inclusive. Otherwise, the result is the empty sequence.
         * </p>
         */
        TZ_FROM_TIME("fn:timezone-from-time", AbstractFunction.class, 1, 1, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:add-yearMonthDurations( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as
         * xs:yearMonthDuration
         * </p>
         * <p>
         * Returns the result of adding the value of $arg1 to the value of $arg2. Backs up the "+" operator on
         * xs:yearMonthDuration values.
         * </p>
         */
        ADD_YMD("op:add-yearMonthDurations", AbstractFunction.class, 2, 2, "xs:yearMonthDuration"),

        /**
         * <p>
         * op:subtract-yearMonthDurations( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as
         * xs:yearMonthDuration
         * </p>
         * <p>
         * Returns the result of subtracting the value of $arg2 from the value of $arg1. Backs up the "-"
         * operator on xs:yearMonthDuration values.
         * </p>
         */
        SUB_YMD("op:subtract-yearMonthDurations", AbstractFunction.class, 2, 2, "xs:yearMonthDuration"),

        /**
         * <p>
         * op:multiply-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:double) as
         * xs:yearMonthDuration
         * </p>
         * <p>
         * Returns the result of multiplying the value of $arg1 by $arg2. The result is rounded to the nearest
         * month. For a value v, 0 <= v < 0.5 rounds to 0; 0.5 <= v < 1.0 rounds to 1.
         * </p>
         */
        MUL_YMD("op:multiply-yearMonthDuration", AbstractFunction.class, 2, 2, "xs:yearMonthDuration"),

        /**
         * <p>
         * op:divide-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:double) as
         * xs:yearMonthDuration
         * </p>
         * <p>
         * Returns the result of dividing the value of $arg1 by $arg2. The result is rounded to the nearest
         * month. For a value v, 0 <= v < 0.5 rounds to 0; 0.5 <= v < 1.0 rounds to 1.
         * </p>
         */
        DIV_YMD("op:divide-yearMonthDuration", AbstractFunction.class, 2, 2, "xs:yearMonthDuration"),

        /**
         * <p>
         * op:divide-yearMonthDuration-by-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as
         * xs:yearMonthDuration) as xs:decimal
         * </p>
         * <p>
         * Returns the result of dividing the value of $arg1 by $arg2. Since the values of both operands are
         * integers, the semantics of the division is identical to /**
         * <p>
         * op:numeric-divide with xs:integer operands.
         * </p>
         */
        DIV_YMD_BY_YMD("op:divide-yearMonthDuration-by-yearMonthDuration", AbstractFunction.class, 2, 2,
            "xs:decimal"),

        /**
         * <p>
         * op:add-dayTimeDurations( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as
         * xs:dayTimeDuration
         * </p>
         * <p>
         * Returns the result of adding the value of $arg1 to the value of $arg2. Backs up the "+" operator on
         * xs:dayTimeDuration values.
         * </p>
         */
        ADD_DTD("op:add-dayTimeDurations", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:subtract-dayTimeDurations( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as
         * xs:dayTimeDuration
         * </p>
         * <p>
         * Returns the result of subtracting the value of $arg2 from the value of $arg1. Backs up the "-"
         * operator on xs:dayTimeDuration values.
         * </p>
         */
        SUB_DTD("op:subtract-dayTimeDurations", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:multiply-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:double) as xs:dayTimeDuration
         * </p>
         * <p>
         * Returns the result of multiplying the value of $arg1 by $arg2.
         * </p>
         */
        MUL_DTD("op:multiply-dayTimeDuration", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:divide-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:double) as xs:dayTimeDuration
         * </p>
         * <p>
         * Returns the result of dividing the value of $arg1 by $arg2.
         * </p>
         */
        DIV_DTD("op:divide-dayTimeDuration", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:divide-dayTimeDuration-by-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as
         * xs:dayTimeDuration) as xs:decimal
         * </p>
         * <p>
         * Returns the result of dividing the value of $arg1 by $arg2. Since the values of both operands are
         * decimals, the semantics of the division is identical to op:numeric-divide with xs:decimal operands.
         * </p>
         */
        DIV_DTD_BY_DTD("op:divide-dayTimeDuration-by-dayTimeDuration", AbstractFunction.class, 2, 2,
            "xs:decimal"),

        /**
         * <p>
         * fn:adjust-dateTime-to-timezone($arg as xs:dateTime?) as xs:dateTime?
         * </p>
         * <p>
         * fn:adjust-dateTime-to-timezone( $arg as xs:dateTime?, $timezone as xs:dayTimeDuration?) as
         * xs:dateTime?
         * </p>
         * <p>
         * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If $timezone is the
         * empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a
         * timezone.
         * </p>
         */
        ADJUST_DT2TZ("fn:adjust-dateTime-to-timezone", AbstractFunction.class, 1, 2, "xs:dateTime"),

        /**
         * <p>
         * fn:adjust-date-to-timezone($arg as xs:date?) as xs:date?
         * </p>
         * <p>
         * fn:adjust-date-to-timezone( $arg as xs:date?, $timezone as xs:dayTimeDuration?) as xs:date?
         * </p>
         * <p>
         * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If $timezone is the
         * empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a
         * timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time
         * 00:00:00.
         * </p>
         */
        ADJUST_DATE2TZ("fn:adjust-date-to-timezone", AbstractFunction.class, 1, 2, "xs:date"),

        /**
         * <p>
         * fn:adjust-time-to-timezone($arg as xs:time?) as xs:time?
         * </p>
         * <p>
         * fn:adjust-time-to-timezone( $arg as xs:time?, $timezone as xs:dayTimeDuration?) as xs:time?
         * </p>
         * <p>
         * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If $timezone is the
         * empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a
         * timezone.
         * </p>
         */
        ADJUST_TIME2TZ("fn:adjust-time-to-timezone", AbstractFunction.class, 1, 2, "xs:time"),

        /**
         * <p>
         * op:subtract-dateTimes( $arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:dayTimeDuration?
         * </p>
         * <p>
         * Returns the xs:dayTimeDuration that corresponds to the difference between the normalized value of
         * $arg1 and the normalized value of $arg2. If either $arg1 or $arg2 do not contain an explicit
         * timezone then, for the purpose of the operation, the implicit timezone provided by the dynamic
         * context (See Section C.2 Dynamic Context ComponentsXP.) is assumed to be present as part of the
         * value.
         * </p>
         */
        SUB_DT("op:subtract-dateTimes", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:subtract-dates($arg1 as xs:date, $arg2 as xs:date) as xs:dayTimeDuration?
         * </p>
         * <p>
         * Returns the xs:dayTimeDuration that corresponds to the difference between the starting instant of
         * $arg1 and the the starting instant of $arg2. If either $arg1 or $arg2 do not contain an explicit
         * timezone then, for the purpose of the operation, the implicit timezone provided by the dynamic
         * context (See Section C.2 Dynamic Context ComponentsXP.) is assumed to be present as part of the
         * value.
         * </p>
         */
        SUB_DATES("op:subtract-dates", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:subtract-times($arg1 as xs:time, $arg2 as xs:time) as xs:dayTimeDuration
         * </p>
         * <p>
         * Returns the xs:dayTimeDuration that corresponds to the difference between the value of $arg1
         * converted to an xs:dateTime using the date components from the reference xs:dateTime and the value
         * of $arg2 converted to an xs:dateTime using the date components from the same reference xs:dateTime
         * . If either $arg1 or $arg2 do not contain an explicit timezone then, for the purpose of the
         * operation, the implicit timezone provided by the dynamic context (See Section C.2 Dynamic Context
         * ComponentsXP.) is assumed to be present as part of the value.
         * </p>
         */
        SUB_TIMES("op:subtract-times", AbstractFunction.class, 2, 2, "xs:dayTimeDuration"),

        /**
         * <p>
         * op:add-yearMonthDuration-to-dateTime( $arg1 as xs:dateTime, $arg2 as xs:yearMonthDuration) as
         * xs:dateTime
         * </p>
         * <p>
         * Returns the xs:dateTime computed by adding $arg2 to the value of $arg1 using the algorithm
         * described in Appendix E of [XML Schema Part 2: Datatypes Second Edition] disregarding the rule
         * about leap seconds. If $arg2 is negative, then the result xs:dateTime precedes $arg1.
         * </p>
         */
        ADD_YMD2DT("op:add-yearMonthDuration-to-dateTime", AbstractFunction.class, 2, 2, "xs:dateTime"),

        /**
         * <p>
         * op:add-dayTimeDuration-to-dateTime( $arg1 as xs:dateTime, $arg2 as xs:dayTimeDuration) as
         * xs:dateTime
         * </p>
         * <p>
         * Returns the xs:dateTime computed by adding $arg2 to the value of $arg1 using the algorithm
         * described in Appendix E of [XML Schema Part 2: Datatypes Second Edition] disregarding the rule
         * about leap seconds. If $arg2 is negative, then the result xs:dateTime precedes $arg1.
         * </p>
         */
        ADD_DTD2DT("op:add-dayTimeDuration-to-dateTime", AbstractFunction.class, 2, 2, "xs:dateTime"),

        /**
         * <p>
         * op:subtract-yearMonthDuration-from-dateTime( $arg1 as xs:dateTime, $arg2 as xs:yearMonthDuration)
         * as xs:dateTime
         * </p>
         * <p>
         * Returns the xs:dateTime computed by negating $arg2 and adding the result to the value of $arg1
         * using the algorithm described in Appendix E of [XML Schema Part 2: Datatypes Second Edition]
         * disregarding the rule about leap seconds. If $arg2 is negative, then the xs:dateTime returned
         * follows $arg1.
         * </p>
         */
        SUB_YMD_FROM_DT("op:subtract-yearMonthDuration-from-dateTime", AbstractFunction.class, 2, 2,
            "xs:dateTime"),

        /**
         * <p>
         * op:subtract-dayTimeDuration-from-dateTime( $arg1 as xs:dateTime, $arg2 as xs:dayTimeDuration) as
         * xs:dateTime
         * </p>
         * <p>
         * Returns the xs:dateTime computed by negating $arg2 and adding the result to the value of $arg1
         * using the algorithm described in Appendix E of [XML Schema Part 2: Datatypes Second Edition]
         * disregarding the rule about leap seconds. If $arg2 is negative, then the xs:dateTime returned
         * follows $arg1.
         * </p>
         */
        SUB_DTD_FROM_DT("op:subtract-dayTimeDuration-from-dateTime", AbstractFunction.class, 2, 2,
            "xs:dateTime"),

        /**
         * <p>
         * op:add-yearMonthDuration-to-date( $arg1 as xs:date, $arg2 as xs:yearMonthDuration) as xs:date
         * </p>
         * <p>
         * Returns the xs:date computed by adding $arg2 to the starting instant of $arg1 using the algorithm
         * described in Appendix E of [XML Schema Part 2: Datatypes Second Edition] and discarding the time
         * components from the resulting xs:dateTime. If $arg2 is negative, then the xs:date returned precedes
         * $arg1.
         * </p>
         */
        ADD_YMD2DATE("op:add-yearMonthDuration-to-date", AbstractFunction.class, 2, 2, "xs:date"),

        /**
         * <p>
         * op:add-dayTimeDuration-to-date( $arg1 as xs:date, $arg2 as xs:dayTimeDuration) as xs:date
         * </p>
         * <p>
         * Returns the xs:date computed by adding $arg2 to the starting instant of $arg1 using the algorithm
         * described in Appendix E of [XML Schema Part 2: Datatypes Second Edition] and discarding the time
         * components from the resulting xs:dateTime. If $arg2 is negative, then the xs:date returned precedes
         * $arg1.
         * </p>
         */
        ADD_DTD2DATE("op:add-dayTimeDuration-to-date", AbstractFunction.class, 2, 2, "xs:date"),

        /**
         * <p>
         * op:subtract-yearMonthDuration-from-date( $arg1 as xs:date, $arg2 as xs:yearMonthDuration) as
         * xs:date
         * </p>
         * <p>
         * Returns the xs:date computed by negating $arg2 and adding the result to the starting instant of
         * $arg1 using the algorithm described in Appendix E of [XML Schema Part 2: Datatypes Second Edition]
         * and discarding the time components from the resulting xs:dateTime. If $arg2 is positive, then the
         * xs:date returned precedes $arg1.
         * </p>
         */
        SUB_YMD_FROM_DATE("op:subtract-yearMonthDuration-from-date", AbstractFunction.class, 2, 2, "xs:date"),

        /**
         * <p>
         * op:subtract-dayTimeDuration-from-date( $arg1 as xs:date, $arg2 as xs:dayTimeDuration) as xs:date
         * </p>
         * <p>
         * Returns the xs:date computed by negating $arg2 and adding the result to the starting instant of
         * $arg1 using the algorithm described in Appendix E of [XML Schema Part 2: Datatypes Second Edition]
         * and discarding the time components from the resulting xs:dateTime. If $arg2 is positive, then the
         * xs:date returned precedes $arg1.
         * </p>
         */
        SUB_DTD_FROM_DATE("op:subtract-dayTimeDuration-from-date", AbstractFunction.class, 2, 2, "xs:date"),

        /**
         * <p>
         * op:add-dayTimeDuration-to-time( $arg1 as xs:time, $arg2 as xs:dayTimeDuration) as xs:time
         * </p>
         * <p>
         * First, the days component in the canonical lexical representation of $arg2 is set to zero (0) and
         * the value of the resulting xs:dayTimeDuration is calculated. Alternatively, the value of $arg2
         * modulus 86,400 is used as the second argument. This value is added to the value of $arg1 converted
         * to an xs:dateTime using a reference date such as 1972-12-31 and the time components of the result
         * returned. Note that the xs:time returned may occur in a following or preceding day and may be less
         * than $arg1.
         * </p>
         */
        ADD_DTD2TIME("op:add-dayTimeDuration-to-time", AbstractFunction.class, 2, 2, "xs:time"),

        /**
         * <p>
         * op:subtract-dayTimeDuration-from-time( $arg1 as xs:time, $arg2 as xs:dayTimeDuration) as xs:time
         * </p>
         * <p>
         * The result is calculated by first setting the day component in the canonical lexical representation
         * of $arg2 to zero (0) and calculating the value of the resulting xs:dayTimeDuration. Alternatively,
         * the value of $arg2 modulus 86,400 is used as the second argument. This value is subtracted from the
         * value of $arg1 converted to an xs:dateTime using a reference date such as 1972-12-31 and the time
         * components of the result are returned. Note that the xs:time returned may occur in a preceding or
         * following day and may be greater than $arg1.
         * </p>
         */
        SUB_DTD_FROM_TIME("op:subtract-dayTimeDuration-from-time", AbstractFunction.class, 2, 2, "xs:time"),

        /**
         * <p>
         * fn:resolve-QName($qname as xs:string?, $element as element()) as xs:QName?
         * </p>
         * <p>
         * Returns an xs:QName value (that is, an expanded-QName) by taking an xs:string that has the lexical
         * form of an xs:QName (a string in the form "prefix:local-name" or "local-name") and resolving it
         * using the in-scope namespaces for a given element.
         * </p>
         */
        RESOLVE_QNAME("fn:resolve-QName", AbstractFunction.class, 2, 2, "xs:QName"),

        /**
         * <p>
         * fn:QName($argumentsURI as xs:string?, $argumentsQName as xs:string) as xs:QName
         * </p>
         * <p>
         * Returns an xs:QName with the namespace URI given in $argumentsURI. If $argumentsURI is the
         * zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value
         * of $argumentsQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence
         * of a prefix) in $argumentsQName is retained in the returned xs:QName value. The local name in the
         * result is taken from the local part of $argumentsQName.
         * </p>
         */
        QNAME("fn:QName", AbstractFunction.class, 2, 2, "xs:QName"),

        /**
         * <p>
         * op:QName-equal($arg1 as xs:QName, $arg2 as xs:QName) as xs:boolean
         * </p>
         * <p>
         * Returns true if the namespace URIs of $arg1 and $arg2 are equal and the local names of $arg1 and
         * $arg2 are identical based on the Unicode code point collation
         * (http://www.w3.org/2005/xpath-functions/collation/codepoint). Otherwise, returns false. Two
         * namespace URIs are considered equal if they are either both absent or both present and identical
         * based on the Unicode code point collation. The prefix parts of $arg1 and $arg2, if any, are
         * ignored.
         * </p>
         */
        QNAME_EQ("op:QName-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * fn:namespace-uri-from-QName($arg as xs:QName?) as xs:anyURI?
         * </p>
         * <p>
         * Returns the namespace URI for $arg as an xs:string. If $arg is the empty sequence, the empty
         * sequence is returned. If $arg is in no namespace, the zero-length string is returned.
         * </p>
         */
        NS_URI_FROM_QNAME("fn:namespace-uri-from-QName", AbstractFunction.class, 1, 1, "xs:anyURI"),

        /**
         * <p>
         * fn:namespace-uri-for-prefix( $prefix as xs:string?, $element as element()) as xs:anyURI?
         * </p>
         * <p>
         * Returns the namespace URI of one of the in-scope namespaces for $element, identified by its
         * namespace prefix.
         * </p>
         */
        NS_URI4PREF("fn:namespace-uri-for-prefix", AbstractFunction.class, 2, 2, "xs:anyURI"),

        /**
         * <p>
         * fn:in-scope-prefixes($element as element()) as xs:string
         * </p>
         * <p>
         * Returns the prefixes of the in-scope namespaces for $element. For namespaces that have a prefix, it
         * returns the prefix as an xs:NCName. For the default namespace, which has no prefix, it returns the
         * zero-length string.
         * </p>
         */
        IN_SCOPE_PREF("fn:in-scope-prefixes", AbstractFunction.class, 1, 1, "xs:string"),

        /**
         * <p>
         * op:hexBinary-equal( $value1 as xs:hexBinary, $value2 as xs:hexBinary) as xs:boolean
         * </p>
         * <p>
         * Returns true if $value1 and $value2 are of the same length, measured in binary octets, and contain
         * the same octets in the same order. Otherwise, returns false.
         * </p>
         */
        HEXBIN_EQ("op:hexBinary-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:base64Binary-equal( $value1 as xs:base64Binary, $value2 as xs:base64Binary) as xs:boolean
         * </p>
         * <p>
         * Returns true if $value1 and $value2 are of the same length, measured in binary octets, and contain
         * the same octets in the same order. Otherwise, returns false.
         * </p>
         */
        BASE64BIN_EQ("op:base64Binary-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:NOTATION-equal($arg1 as xs:NOTATION, $arg2 as xs:NOTATION) as xs:boolean
         * </p>
         * <p>
         * Returns true if the namespace URIs of $arg1 and $arg2 are equal and the local names of $arg1 and
         * $arg2 are identical based on the Unicode code point collation:
         * http://www.w3.org/2005/xpath-functions/collation/codepoint. Otherwise, returns false. Two namespace
         * URIs are considered equal if they are either both absent or both present and identical based on the
         * Unicode code point collation. The prefix parts of $arg1 and $arg2, if any, are ignored.
         * </p>
         */
        NOTATION_EQ("op:NOTATION-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * fn:name() as xs:string
         * </p>
         * <p>
         * fn:name($arg as node()?) as xs:string
         * </p>
         * <p>
         * Returns the name of a node, as an xs:string that is either the zero-length string, or has the
         * lexical form of an xs:QName.
         * </p>
         */
        NAME("fn:name", AbstractFunction.class, 0, 1, " xs:string"),

        /**
         * <p>
         * fn:local-name() as xs:string
         * </p>
         * <p>
         * fn:local-name($arg as node()?) as xs:string
         * </p>
         * <p>
         * Returns the local part of the name of $arg as an xs:string that will either be the zero-length
         * string or will have the lexical form of an xs:NCName.
         * </p>
         */
        LOC_NAME("fn:local-name", AbstractFunction.class, 0, 1, " xs:string"),

        /**
         * <p>
         * fn:local-name-from-QName($arg as xs:QName?) as xs:NCName?
         * </p>
         * <p>
         * Returns an xs:NCName representing the local part of $arg. If $arg is the empty sequence, returns
         * the empty sequence.
         * </p>
         */
        LOC_NAME_FROM_QNAME("fn:local-name-from-QName", AbstractFunction.class, 1, 1, "xs:NCName"),

        /**
         * <p>
         * fn:namespace-uri() as xs:anyURI
         * </p>
         * <p>
         * fn:namespace-uri($arg as node()?) as xs:anyURI
         * </p>
         * <p>
         * Returns the namespace URI of the xs:QName of $arg.
         * </p>
         */
        NS_URI("fn:namespace-uri", AbstractFunction.class, 0, 1, "xs:anyURI"),

        /**
         * <p>
         * fn:number() as xs:double fn:number($arg as xs:anyAtomicType?) as xs:double.
         * </p>
         * <p>
         * Returns the value indicated by $arg or, if $arg is not specified, the context item after
         * atomization, converted to an xs:double
         * </p>
         */
        NUMBER("fn:number", AbstractFunction.class, 1, 1, "xs:double"),

        /**
         * <p>
         * fn:lang($testlang as xs:string?) as xs:boolean
         * </p>
         * <p>
         * fn:lang($testlang as xs:string?, $node as node()) as xs:boolean
         * </p>
         * <p>
         * This function tests whether the language of $node, or the context item if the second argument is
         * omitted, as specified by xml:lang attributes is the same as, or is a sublanguage of, the language
         * specified by $testlang. The behavior of the function if the second argument is omitted is exactly
         * the same as if the context item (.) had been passed as the second argument. The language of the
         * argument node, or the context item if the second argument is omitted, is determined by the value of
         * the xml:lang attribute on the node, or, if the node has no such attribute, by the value of the
         * xml:lang attribute on the nearest ancestor of the node that has an xml:lang attribute. If there is
         * no such ancestor, then the function returns false
         * </p>
         */
        LANG("fn:lang", AbstractFunction.class, 1, 2, "xs:boolean"),

        /**
         * <p>
         * op:is-same-node($argumentseter1 as node(), $argumentseter2 as node()) as xs:boolean
         * </p>
         * <p>
         * If the node identified by the value of $argumentseter1 is the same node as the node identified by
         * the value of $argumentseter2 (that is, the two nodes have the same identity), then the function
         * returns true; otherwise, the function returns false. This function backs up the "is" operator on
         * nodes.
         * </p>
         */
        IS_SAME_NODE("op:is-same-node", AbstractFunction.class, 2, 2, "xs:boolean"),

/**
   * <p>
   * op:node-before($argumentseter1 as node(), $argumentseter2 as node()) as
   * xs:boolean
   * </p>
   * <p>
   * If the node identified by the value of $argumentseter1 occurs in document
   * order before the node identified by the value of $argumentseter2, this
   * function returns true; otherwise, it returns false. The rules determining
   * the order of nodes within a single document and in different documents can
   * be found in Section 2.4 Document OrderDM. This function backs up the "<<"
   * operator.
   * </p>
   */
        NODE_BEFORE("op:node-before", AbstractFunction.class, 2, 2, "xs:boolean"),

        /**
         * <p>
         * op:node-after($argumentseter1 as node(), $argumentseter2 as node()) as xs:boolean
         * </p>
         * <p>
         * If the node identified by the value of $argumentseter1 occurs in document order after the node
         * identified by the value of $argumentseter2, this function returns true; otherwise, it returns
         * false. The rules determining the order of nodes within a single document and in different documents
         * can be found in Section 2.4 Document OrderDM. This function backs up the ">>" operator.
         * </p>
         */
        NODE_AFTER("op:node-after", AbstractFunction.class, 2, 2, "xs:boolean"),

        // /**
        // * <p>
        // * fn:root() as node()
        // * </p>
        // * <p>
        // * fn:root($arg as node()?) as node()?
        // * </p>
        // * <p>
        // * Returns the root of the tree to which $arg belongs. This will usually,
        // but
        // * not necessarily, be a document node.fn:boolean($arg as item()*) as
        // * xs:boolean
        // * </p>
        // */
        // ROOT("fn:root", AbstractFunction.class, 0, 1),

        /**
         * <p>
         * fn:boolean($arg as item()*) as xs:boolean
         * </p>
         * <p>
         * Computes the effective boolean value of the sequence $arg. See Section 2.4.3 Effective Boolean
         * ValueXP
         * </p>
         */
        BOOLEAN("fn:boolean", AbstractFunction.class, 1, 1, "xs:boolean"),

        // /**
        // * <p>
        // * op:concatenate($seq1 as item()*, $seq2 as item()*) as item()
        // * </p>
        // * <p>
        // * Returns a sequence consisting of the items in $seq1 followed by the
        // items
        // * in $seq2. This function backs up the infix operator ",
        // AbstractFunction.class, ". If either
        // sequence
        // * is the empty sequence, the other operand is returned.
        // * </p>
        // */
        // CONCATE("op:concatenate", AbstractFunction.class, 2, 2),
        //
        /**
         * <p>
         * fn:index-of( $seqarguments as xs:anyAtomicType*, $srcharguments as xs:anyAtomicType) as xs:integer*
         * </p>
         * <p>
         * fn:index-of( $seqarguments as xs:anyAtomicType*, $srcharguments as xs:anyAtomicType, $collation as
         * xs:string) as xs:integer*
         * </p>
         * <p>
         * Returns a sequence of positive integers giving the positions within the sequence $seqarguments of
         * items that are equal to $srcharguments.
         * </p>
         */
        INDEX_OF("fn:index-of", AbstractFunction.class, 2, 3, "xs:integer"),

        /**
         * <p>
         * fn:empty($arg as item()*) as xs:boolean
         * </p>
         * <p>
         * If the value of $arg is the empty sequence, the function returns true; otherwise, the function
         * returns false.
         * </p>
         */
        EMPTY("fn:empty", AbstractFunction.class, 1, 1, "xs:boolean"),

        /**
         * <p>
         * fn:exists($arg as item()*) as xs:boolean
         * </p>
         * <p>
         * If the value of $arg is not the empty sequence, the function returns true; otherwise, the function
         * returns false.
         * </p>
         */
        EXISTES("fn:exists", AbstractFunction.class, 1, 1, "xs:boolean"),

        /**
         * <p>
         * fn:distinct-values($arg as xs:anyAtomicType*) as xs:anyAtomicType
         * </p>
         * <p>
         * fn:distinct-values( $arg as xs:anyAtomicType*, $collation as xs:string) as xs:anyAtomicType*
         * </p>
         * <p>
         * Returns the sequence that results from removing from $arg all but one of a set of values that are
         * eq to one other. Values of type xs:untypedAtomic are compared as if they were of type xs:string.
         * Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered
         * to be distinct. The order in which the sequence of values is returned is �implementation
         * dependent�.
         * </p>
         */
        DIST_VALUES("fn:distinct-values", AbstractFunction.class, 1, 2, "xs:anyAtomicType"),
        //
        // /**
        // * <p>
        // * fn:insert-before( $target as item()*, $position as xs:integer, $inserts
        // as
        // * item()*) as item()*
        // * </p>
        // * <p>
        // * Returns a new sequence constructed from the value of $target with the
        // value
        // * of $inserts inserted at the position specified by the value of
        // $position.
        // * (The value of $target is not affected by the sequence construction.)
        // * </p>
        // */
        // INSERT_BEFORE("fn:insert-before", AbstractFunction.class, 3, 3),
        //
        // /**
        // * <p>
        // * fn:remove($target as item()*, $position as xs:integer) as item()
        // * </p>
        // * <p>
        // * Returns a new sequence constructed from the value of $target with the
        // item
        // * at the position specified by the value of $position removed.
        // * </p>
        // */
        // REMOVE("fn:remove", AbstractFunction.class, 2, 2),
        //
        // /**
        // * <p>
        // * fn:reverse($arg as item()*) as item()
        // * </p>
        // * <p>
        // * Reverses the order of items in a sequence. If $arg is the empty
        // sequence,
        // * the empty sequence is returned.
        // * </p>
        // */
        // REVERSE("fn:reverse", AbstractFunction.class, 1, 1),
        //
        // /**
        // * <p>
        // * fn:subsequence($sourceSeq as item()*, $startingLoc as xs:double) as
        // item()
        // * <p>
        // * </p>
        // * fn:subsequence( $sourceSeq as item()*, $startingLoc as xs:double,
        // $length
        // * as xs:double) as item()*
        // * </p>
        // * <p>
        // * Returns the contiguous sequence of items in the value of $sourceSeq
        // * beginning at the position indicated by the value of $startingLoc and
        // * continuing for the number of items indicated by the value of $length.
        // * </p>
        // */
        // SUBSEQ("fn:subsequence", AbstractFunction.class, 2, 3),
        //
        // /**
        // * <p>
        // * fn:unordered($sourceSeq as item()*) as item()
        // * </p>
        // * <p>
        // * Returns the items of $sourceSeq in an �implementation dependent�
        // order.
        // * </p>
        // */
        // UNORDERED("fn:unordered", AbstractFunction.class, 1, 1),
        //
        // /**
        // * <p>
        // * fn:zero-or-one($arg as item()*) as item()?
        // * </p>
        // * <p>
        // * Returns $arg if it contains zero or one items. Otherwise, raises an
        // error
        // * [err:FORG0003].
        // * </p>
        // */
        // ZERO_OR_ONE("fn:zero-or-one", AbstractFunction.class, FNCount.class, 1,
        // 1,
        // ),

        // /**
        // * <p>
        // * fn:one-or-more($arg as item()*) as item()+
        // * </p>
        // * <p>
        // * Returns $arg if it contains one or more items. Otherwise, raises an
        // error
        // * [err:FORG0004].
        // * </p>
        // */
        // ONE_OR_MORE("fn:one-or-more", AbstractFunction.class, FNCount.class, 1,
        // 1),

        // /**
        // * <p>
        // * fn:exactly-one($arg as item()*) as item()
        // * </p>
        // * <p>
        // * Returns $arg if it contains exactly one item. Otherwise, raises an
        // error
        // * [err:FORG0005].
        // * </p>
        // */
        // EXACTLY_ONE("fn:exactly-one", AbstractFunction.class, FNCount.class, 1,
        // 1),
        //
        /**
         * <p>
         * fn:deep-equal($argumentseter1 as item()*, $argumentseter2 as item()*) as xs:boolean
         * </p>
         * <p>
         * fn:deep-equal( $argumentseter1 as item()*, $argumentseter2 as item()*, $collation as string) as
         * xs:boolean
         * </p>
         * <p>
         * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they
         * must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must
         * either be atomic values that compare equal, or nodes of the same kind, with the same name, whose
         * children are deep-equal. This is defined in more detail below. The $collation argument identifies a
         * collation which is used at all levels of recursion when strings are compared (but not when names
         * are compared), according to the rules in 7.3.1 Collations.
         * </p>
         */
        DEEP_EQ("fn:deep-equal", AbstractFunction.class, 2, 2, "xs:boolean"),

        // /**
        // * <p>
        // * op:union($argumentseter1 as node()*, $argumentseter2 as node()*) as
        // node()
        // * </p>
        // * <p>
        // * Constructs a sequence containing every node that occurs in the values
        // of
        // * either $argumentseter1 or $argumentseter2, eliminating duplicate nodes.
        // * Nodes are returned in document order. Two nodes are duplicates if they
        // are
        // * op:is-same-node().
        // * </p>
        // */
        // UNION("op:union", AbstractFunction.class, 2, 2),
        //
        // /**
        // * <p>
        // * op:intersect($argumentseter1 as node()*, $argumentseter2 as node()*) as
        // * node()
        // * </p>
        // * <p>
        // * Constructs a sequence containing every node that occurs in the values
        // of
        // * both $argumentseter1 and $argumentseter2, eliminating duplicate nodes.
        // * Nodes are returned in document order.
        // * </p>
        // */
        // INTERSECT("op:intersect", AbstractFunction.class, 2, 2),
        //
        // /**
        // * <p>
        // * op:except($argumentseter1 as node()*, $argumentseter2 as node()*) as
        // node()
        // * </p>
        // * <p>
        // * Constructs a sequence containing every node that occurs in the value of
        // * $argumentseter1, but not in the value of $argumentseter2, eliminating
        // * duplicate nodes. Nodes are returned in document order.
        // * </p>
        // */
        // EXCEPT("op:except", AbstractFunction.class, 2, 2),

        /**
         * <p>
         * fn:count($arg as item()*) as xs:integer
         * </p>
         * <p>
         * Returns the number of items in the value of $arg.
         * </p>
         */
        COUNT("fn:count", FNCount.class, 1, 2, "xs:integer"),

        /**
         * <p>
         * fn:avg($arg as xs:anyAtomicType*) as xs:anyAtomicType?
         * </p>
         * <p>
         * Returns the average of the values in the input sequence $arg, that is, the sum of the values
         * divided by the number of values.
         * </p>
         */
        AVG("fn:avg", AbstractFunction.class, 1, 1, "xs:anyAtomicType"),

        /**
         * <p>
         * fn:max($arg as xs:anyAtomicType*) as xs:anyAtomicType?
         * </p>
         * <p>
         * fn:max($arg as xs:anyAtomicType*, $collation as string) as xs:anyAtomicType?
         * </p>
         * <p>
         * Selects an item from the input sequence $arg whose value is greater than or equal to the value of
         * every other item in the input sequence. If there are two or more such items, then the specific item
         * whose value is returned is �implementation dependent�.
         * </p>
         */
        MAX("fn:max", AbstractFunction.class, 1, 2, "xs:anyAtomicType"),

        /**
         * <p>
         * fn:min($arg as xs:anyAtomicType*) as xs:anyAtomicType?
         * </p>
         * <p>
         * fn:min($arg as xs:anyAtomicType*, $collation as string) as xs:anyAtomicType?
         * </p>
         * <p>
         * selects an item from the input sequence $arg whose value is less than or equal to the value of
         * every other item in the input sequence. If there are two or more such items, then the specific item
         * whose value is returned is �implementation dependent�.
         * </p>
         */
        MIN("fn:min", AbstractFunction.class, 1, 2, "xs:anyAtomicType"),
        //
        /**
         * <p>
         * fn:sum($arg as xs:anyAtomicType*) as xs:anyAtomicType
         * </p>
         * <p>
         * fn:sum( $arg as xs:anyAtomicType*, $zero as xs:anyAtomicType?) as xs:anyAtomicType?
         * </p>
         * <p>
         * Returns a value obtained by adding together the values in $arg. If $zero is not specified, then the
         * value returned for an empty sequence is the xs:integer value 0. If $zero is specified, then the
         * value returned for an empty sequence is $zero.
         * </p>
         */
        SUM("fn:sum", FNCount.class, 1, 2, "xs:anyAtomicType"),

        /**
         * <p>
         * op:to($firstval as xs:integer, $lastval as xs:integer) as xs:integer
         * </p>
         * <p>
         * Returns the sequence containing every xs:integer whose value is between the value of $firstval
         * (inclusive) and the value of $lastval (inclusive), in monotonic order. If the value of the first
         * operand is greater than the value of the second, the empty sequence is returned. If the values of
         * the two operands are equal, a sequence containing a single xs:integer equal to the value is
         * returned.
         * </p>
         */
        TO("op:to", AbstractFunction.class, 2, 2, "xs:integer"),
        //
        // /**
        // * <p>
        // * fn:id($arg as xs:string*) as element()*
        // * </p>
        // * <p>
        // * fn:id($arg as xs:string*, $node as node()) as element()*
        // * </p>
        // * <p>
        // * Returns the sequence of element nodes that have an ID value matching
        // the
        // * value of one or more of the IDREF values supplied in $arg .
        // * </p>
        // */
        // ID("fn:id", AbstractFunction.class, 1, 2),
        //
        // /**
        // * <p>
        // * fn:idref($arg as xs:string*) as node()*
        // * </p>
        // * <p>
        // * fn:idref($arg as xs:string*, $node as node()) as node()*
        // * </p>
        // * <p>
        // * Returns the sequence of element or attribute nodes with an IDREF value
        // * matching the value of one or more of the ID values supplied in $arg.
        // * </p>
        // */
        // IDREF("fn:idref", AbstractFunction.class, 1, 2),
        //
        // /**
        // * <p>
        // * fn:doc($uri as xs:string?) as document-node()?
        // * </p>
        // * <p>
        // * Retrieves a document using an xs:anyURI, which may include a fragment
        // * identifier, supplied as an xs:string. If $uri is not a valid xs:anyURI,
        // an
        // * error is raised [err:FODC0005]. If it is a relative URI Reference, it
        // is
        // * resolved relative to the value of the base URI property from the static
        // * context. The resulting absolute URI Reference is promoted to an
        // xs:string.
        // * If the Available documents discussed in Section 2.1.2 Dynamic ContextXP
        // * provides a mapping from this string to a document node, the function
        // * returns that document node. If the Available documents provides no
        // mapping
        // * for the string, an error is raised [err:FODC0005].
        // * </p>
        // */
        // DOC("fn:doc", AbstractFunction.class, 1, 1),
        //
        /**
         * <p>
         * fn:doc-available($uri as xs:string?) as xs:boolean
         * </p>
         * <p>
         * If fn:doc($uri) returns a document node, this function returns true. If $uri is not a valid
         * xs:anyURI, an error is raised [err:FODC0005]. Otherwise, this function returns false.
         * </p>
         */
        DOC_AVAILABLE("fn:doc-available", AbstractFunction.class, 1, 1, "xs:boolean"),

        // /**
        // * <p>
        // * fn:collection() as node()*
        // * </p>
        // * <p>
        // * fn:collection($arg as xs:string?) as node()*
        // * </p>
        // * <p>
        // * This function takes an xs:string as argument and returns a sequence of
        // * nodes obtained by interpreting $arg as an xs:anyURI and resolving it
        // * according to the mapping specified in Available collections described
        // in
        // * Section C.2 Dynamic Context ComponentsXP. If Available collections
        // provides
        // * a mapping from this string to a sequence of nodes, the function returns
        // * that sequence. If Available collections maps the string to an empty
        // * sequence, then the function returns an empty sequence. If Available
        // * collections provides no mapping for the string, an error is raised
        // * [err:FODC0004]. If $arg is not specified, the function returns the
        // sequence
        // * of the nodes in the default collection in the dynamic context. See
        // Section
        // * C.2 Dynamic Context ComponentsXP. If the value of the default
        // collection
        // is
        // * undefined an error is raised [err:FODC0002].
        // * </p>
        // */
        // COLLECTION("fn:collection", AbstractFunction.class, 0, 1),
        //
        /**
         * <p>
         * fn:position() as xs:integer
         * </p>
         * <p>
         * Returns the context position from the dynamic context. (See Section C.2 Dynamic Context
         * ComponentsXP.) If the context item is undefined, an error is raised: [err:XPDY0002]XP.
         * </p>
         */
        POS("fn:position", AbstractFunction.class, 0, 0, "xs:integer"),

        /**
         * <p>
         * fn:last() as xs:integer
         * </p>
         * <p>
         * Returns the context size from the dynamic context. (See Section C.2 Dynamic Context ComponentsXP.)
         * If the context item is undefined, an error is raised: [err:XPDY0002]XP.
         * </p>
         */
        LAST("fn:last", AbstractFunction.class, 0, 0, "xs:integer"),

        /**
         * <p>
         * fn:current-dateTime() as xs:dateTime
         * </p>
         * <p>
         * Returns the current dateTime (with timezone) from the dynamic context. (See Section C.2 Dynamic
         * Context ComponentsXP.) This is an xs:dateTime that is current at some time during the evaluation of
         * a query or transformation in which fn:current-dateTime() is executed. This function is
         * �stable�. The precise instant during the query or transformation represented by the value of
         * fn:current-dateTime() is �implementation dependent�.
         * </p>
         */
        CURRENT_DT("fn:current-dateTime", AbstractFunction.class, 0, 0, "xs:dateTime"),

        /**
         * <p>
         * fn:current-date() as xs:date
         * </p>
         * <p>
         * Returns xs:date(fn:current-dateTime()). This is an xs:date (with timezone) that is current at some
         * time during the evaluation of a query or transformation in which fn:current-date() is executed.
         * This function is �stable�. The precise instant during the query or transformation represented
         * by the value of fn:current-date() is �implementation dependent�.
         * </p>
         */
        CURRENT_DATE("fn:current-date", AbstractFunction.class, 0, 0, "xs:date"),

        /**
         * <p>
         * fn:current-time() as xs:time
         * </p>
         * <p>
         * Returns xs:time(fn:current-dateTime()). This is an xs:time (with timezone) that is current at some
         * time during the evaluation of a query or transformation in which fn:current-time() is executed.
         * This function is �stable�. The precise instant during the query or transformation represented
         * by the value of fn:current-time() is �implementation dependent�.
         * </p>
         */
        CURRENT_TIME("fn:current-time", AbstractFunction.class, 0, 0, "xs:time"),

        /**
         * <p>
         * fn:implicit-timezone() as xs:dayTimeDuration
         * </p>
         * <p>
         * Returns the value of the implicit timezone property from the dynamic context. Components of the
         * dynamic context are discussed in Section C.2 Dynamic Context ComponentsXP.
         * </p>
         */
        IMPLICIT_TZ("fn:implicit-timezone", AbstractFunction.class, 0, 0, "xs:dayTimeDuration"),

        /**
         * <p>
         * fn:default-collation() as xs:string
         * </p>
         * <p>
         * Returns the value of the default collation property from the static context. Components of the
         * static context are discussed in Section C.1 Static Context ComponentsXP.
         * </p>
         */
        DEFAULT_COLL("fn:default-collation", AbstractFunction.class, 0, 0, "xs:string"),

        /**
         * <p>
         * fn:static-base-uri() as xs:anyURI?
         * </p>
         * <p>
         * Returns the value of the Base URI property from the static context. If the Base URI property is
         * undefined, the empty sequence is returned. Components of the static context are discussed in
         * Section C.1 Static Context ComponentsXP .
         * </p>
         */
        STATIC_BASE_URI("fn:static-base-uri", AbstractFunction.class, 0, 0, "xs:anyURI");

    /** Name of the function. */
    private final String mName;

    /** Minimum number of possible arguments. */
    private int mMin;

    /** Maximum number of possible arguments. */
    private int mMax;

    /** Return type of the function. */
    private String mReturnType;

    /** The class that implements the function. */
    private Class<? extends AbstractFunction> mFunc;

    /**
     * Constructor. Initializes internal state.
     * 
     * @param mName
     *            qualified name of the function
     * @param mFunc
     *            class that implements the function
     * @param min
     *            specified minimum number of function arguments
     * @param max
     *            specified maximum number of function arguments
     * @param returnType
     *            return type of the function
     */
    private FuncDef(final String mName, final Class<? extends AbstractFunction> mFunc, final int min,
        final int max, final String returnType) {

        this.mName = mName;
        this.mFunc = mFunc;
        this.mMin = min;
        this.mMax = max;
        this.mReturnType = returnType;

    }

    /**
     * @return the string representation of the function's name.
     */
    public String getName() {

        return mName;
    }

    /**
     * @return the class that implements the function
     */
    public Class<? extends AbstractFunction> getFunc() {

        return mFunc;
    }

    /**
     * @return the minimum number of allowed function arguments
     */
    public int getMin() {

        return mMin;
    }

    /**
     * @return the maximum number of allowed function arguments
     */
    public int getMax() {

        return mMax;
    }

    /**
     * @return the specified return type of the function
     */
    public String getReturnType() {

        return mReturnType;
    }

}
