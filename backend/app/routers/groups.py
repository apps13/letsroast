from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import select

from app.db.deps import get_db
from app.db.models import Group
from app.services.invite_codes import generate_invite_code

router = APIRouter(prefix="/groups", tags=["groups"])


@router.post("")
def create_group(payload: dict, db: Session = Depends(get_db)):
    name = (payload.get("name") or "").strip()
    if not name:
        raise HTTPException(status_code=400, detail="Group name is required")

    # Generate invite code; retry if collision.
    for _ in range(5):
        code = generate_invite_code()
        existing = db.scalar(select(Group).where(Group.invite_code == code))
        if existing is None:
            group = Group(name=name, invite_code=code, roast_enabled=False)
            db.add(group)
            db.commit()
            db.refresh(group)
            return {
                "group_id": str(group.id),
                "name": group.name,
                "invite_code": group.invite_code,
                "roast_enabled": group.roast_enabled,
            }

    raise HTTPException(status_code=500, detail="Failed to generate unique invite code")


@router.post("/join")
def join_group(payload: dict, db: Session = Depends(get_db)):
    invite_code = (payload.get("invite_code") or "").strip().upper()
    if not invite_code:
        raise HTTPException(status_code=400, detail="invite_code is required")

    group = db.scalar(select(Group).where(Group.invite_code == invite_code))
    if group is None:
        raise HTTPException(status_code=404, detail="Invalid invite code")

    return {
        "group_id": str(group.id),
        "name": group.name,
        "roast_enabled": group.roast_enabled,
    }
