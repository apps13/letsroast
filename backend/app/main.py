from fastapi import FastAPI

from app.db.session import engine
from app.db.models import Base
from app.routers.groups import router as groups_router

app = FastAPI(title="LetsRoast")

@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)

app.include_router(groups_router)

@app.get("/health")
def health():
    return {"status": "ok"}
