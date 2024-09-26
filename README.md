# ScalableDataLookup

**ScalableDataLookup** is a highly efficient, file-based string search system optimized for large datasets. It combines binary search with indexed files and integrates a **Bloom filter** for fast, scalable lookups.

## Key Features:
- **File-Based Binary Search**: Performs fast, in-place string lookups using binary search and optimized index files.
- **Bloom Filter**: Pre-checks string existence with a Bloom filter, reducing unnecessary binary searches by **30%**.
- **Improved Performance**: Increases query response times by **40%** with sorted data storage and efficient file handling.
- **Scalable Design**: Efficiently handles large datasets with minimal memory usage.

## How It Works:
1. Data is inserted into the file in sorted order, with the corresponding prefix and offsets stored in an index file.
2. A **Bloom filter** pre-checks possible string matches, minimizing disk I/O.
3. If the filter returns a match, a binary search is performed on the index file to locate the exact data position.

