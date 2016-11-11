# AS Tiers Information Extractor

The AS Tiers Information Extract, or as-tiers, is a project designed to extract tier information
from the CAIDA's AS relationship information data.

## Usage

Download the most recent jar file from the release page.

`java -jar as-tiers.jar INPUTFILE OUTPUTFILE`

## Output format

Each line is formated as `TIER:ASN`, where TIER ranges from 1 to 3, and ASN is the AS number.

We define the tiers as follows:
* tier-1: ASes that have no provider ASes;
* tier-3: ASes that have only provider ASes;
* tier-2: other ASes.