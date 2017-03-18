package chessgame;

/**
 Enumerated type that describes the general type that a move is. 
 NOTE: These types are mutually exclusive, unlike promotion, check, and
   checkmate, which could be coincident with one or more of these. 
 NORMAL - Covers most moves that are made
 CASTLE_KS - King castles with the rook closest to him
 CASTLE_QS - King castles with the rook further from him

 @author John Polus
 */
public enum MoveType
{
   NORMAL, CASTLE_KS, CASTLE_QS;
}
